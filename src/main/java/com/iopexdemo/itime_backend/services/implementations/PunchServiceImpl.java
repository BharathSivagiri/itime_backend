package com.iopexdemo.itime_backend.services.implementations;

import com.iopexdemo.itime_backend.dto.*;
import com.iopexdemo.itime_backend.entities.ShiftDetails;
import com.iopexdemo.itime_backend.entities.ShiftRosterDetails;
import com.iopexdemo.itime_backend.entities.WebPunch;
import com.iopexdemo.itime_backend.enums.EnumPunchType;
import com.iopexdemo.itime_backend.enums.EnumRecordStatus;
import com.iopexdemo.itime_backend.mapper.PunchMapper;
import com.iopexdemo.itime_backend.repositories.EmployeeRepository;
import com.iopexdemo.itime_backend.repositories.ShiftRosterDetailsRepository;
import com.iopexdemo.itime_backend.repositories.WebPunchRepository;
import com.iopexdemo.itime_backend.services.PunchService;
import com.iopexdemo.itime_backend.validators.PunchValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PunchServiceImpl implements PunchService {

    private static final Logger logger = LoggerFactory.getLogger(PunchServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final WebPunchRepository webPunchRepository;
    private final PunchMapper punchMapper;
    private final PunchValidator punchValidator;
    private final ShiftRosterDetailsRepository shiftRosterDetailsRepository;

    public void recordPunch(PunchRequest request) {
        logger.info("Validation for employee data and punch request in database started.");
        var employee = punchValidator.getValidatedEmployee(request.getEmployeeId(), employeeRepository);

        punchValidator.validateShiftAndPunchLimit(request.getEmployeeId(), request.getPunchType());

        WebPunch punch = punchMapper.toPunchEntity(request, employee);
        logger.info("Data saved in database after mapped using mapper class.");
        webPunchRepository.save(punch);
    }

    @Override
    public TimeCalculationResponse calculateTime(Integer employeeId, LocalDateTime targetDateTime) {
        // Set target time - use provided time or current time if null
        LocalDateTime currentDateTime = targetDateTime != null ? targetDateTime : LocalDateTime.now();
        LocalDate currentDate = currentDateTime.toLocalDate();

        // Fetch current day's shift roster from database
        ShiftRosterDetails currentRoster = shiftRosterDetailsRepository
                .findByEmployeeIdAndShiftDateAndRecordStatus(employeeId, currentDate, EnumRecordStatus.ACTIVE)
                .orElse(null);

        // Fetch previous day's shift roster from database
        ShiftRosterDetails previousRoster = shiftRosterDetailsRepository
                .findByEmployeeIdAndShiftDateAndRecordStatus(employeeId, currentDate.minusDays(1), EnumRecordStatus.ACTIVE)
                .orElse(null);

        // Calculate current shift start time (ex 18:00 current day)
        LocalDateTime currentShiftStart = currentRoster != null ?
                currentRoster.getShiftDate().atTime(currentRoster.getShiftDetails().getStartTime()) : null;

        // Calculate current shift end time (ex 07:00 next day)
        LocalDateTime currentShiftEnd = currentRoster != null ?
                currentRoster.getShiftDate().plusDays(1).atTime(currentRoster.getShiftDetails().getEndTime()) : null;

        // Calculate previous shift end time (ex 07:00 current day)
        LocalDateTime previousShiftEnd = previousRoster != null ?
                previousRoster.getShiftDate().plusDays(1).atTime(previousRoster.getShiftDetails().getEndTime()) : null;

        // Determine which roster is active based on target time
        // If time is before previous shift end -> use previous roster
        // If time is after current shift start -> use current roster
        // Otherwise return null (time falls between shifts)
        ShiftRosterDetails activeRoster = previousShiftEnd != null && currentDateTime.isBefore(previousShiftEnd) ?
                previousRoster : (currentShiftStart != null && !currentDateTime.isBefore(currentShiftStart) ? currentRoster : null);

        // Return empty response if no active roster found (time between shifts)
        if (activeRoster == null) {
            return TimeCalculationResponse.builder().build();
        }

        // Get shift details and calculate query window
        ShiftDetails shift = activeRoster.getShiftDetails();
        LocalDateTime queryStart = activeRoster.getShiftDate().atTime(shift.getStartTime());
        LocalDateTime queryEnd = activeRoster.getShiftDate().plusDays(1).atTime(shift.getEndTime());

        // Get all punches within shift window, ordered by time
        List<WebPunch> punches = webPunchRepository
                .findByEmployeeIdAndPunchTimeBetweenOrderByPunchTimeAsc(employeeId, queryStart, queryEnd);

        // Validate punch sequence - first punch must be IN
        Optional<WebPunch> firstPunch = punches.stream().findFirst();
        if (firstPunch.isPresent() && !EnumPunchType.IN.equals(firstPunch.get().getPunchType())) {
            return TimeCalculationResponse.builder().build();
        }

        // Get first IN punch of shift
        Optional<WebPunch> firstPunchIn = punches.stream()
                .filter(p -> EnumPunchType.IN.equals(p.getPunchType()))
                .findFirst();

        // Get last OUT punch of shift
        Optional<WebPunch> lastPunchOut = punches.stream()
                .filter(p -> EnumPunchType.OUT.equals(p.getPunchType()))
                .reduce((first, second) -> second);

        // Calculate total hours if both IN and OUT punches exist
        Duration totalHours = (firstPunchIn.isPresent() && lastPunchOut.isPresent()) ?
                Duration.between(firstPunchIn.get().getPunchTime(), lastPunchOut.get().getPunchTime()) : Duration.ZERO;

        // Map all calculated data to response object
        return punchMapper.toTimeCalculationResponse(
                firstPunchIn,
                lastPunchOut,
                totalHours,
                punches.stream().reduce((first, second) -> second),
                shift);
    }

    @Override
    public WeeklyStatsResponse calculateWeeklyStats(Integer employeeId, LocalDate startDate, LocalDate endDate) {
        Duration totalActualHours = Duration.ZERO;
        Duration totalShiftHours = Duration.ZERO;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            ShiftRosterDetails currentRoster = shiftRosterDetailsRepository
                    .findByEmployeeIdAndShiftDateAndRecordStatus(employeeId, currentDate, EnumRecordStatus.ACTIVE)
                    .orElse(null);

            if (currentRoster != null) {
                ShiftDetails shift = currentRoster.getShiftDetails();

                LocalDateTime queryStart = currentRoster.getShiftDate().atTime(shift.getStartTime());
                LocalDateTime queryEnd = currentRoster.getShiftDate().plusDays(1).atTime(shift.getEndTime());

                Duration shiftHours = Duration.between(queryStart, queryEnd);
                totalShiftHours = totalShiftHours.plus(shiftHours);

                List<WebPunch> punches = webPunchRepository
                        .findByEmployeeIdAndPunchTimeBetweenOrderByPunchTimeAsc(
                                employeeId, queryStart, queryEnd);

                Optional<WebPunch> firstPunchIn = punches.stream()
                        .filter(p -> EnumPunchType.IN.equals(p.getPunchType()))
                        .findFirst();

                Optional<WebPunch> lastPunchOut = punches.stream()
                        .filter(p -> EnumPunchType.OUT.equals(p.getPunchType()))
                        .reduce((first, second) -> second);

                if (firstPunchIn.isPresent() && lastPunchOut.isPresent()) {
                    Duration actualHours = Duration.between(
                            firstPunchIn.get().getPunchTime(),
                            lastPunchOut.get().getPunchTime()
                    );
                    totalActualHours = totalActualHours.plus(actualHours);
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        String formattedShiftHours = String.format("%02d:%02d",
                totalShiftHours.toHours(),
                totalShiftHours.toMinutesPart());

        String formattedActualHours = String.format("%02d:%02d",
                totalActualHours.toHours(),
                totalActualHours.toMinutesPart());

        return new WeeklyStatsResponse(startDate, endDate, formattedShiftHours, formattedActualHours);
    }

}



