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
import java.util.stream.Collectors;

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
        LocalDateTime currentDateTime = targetDateTime != null ? targetDateTime : LocalDateTime.now();
        LocalDate currentDate = currentDateTime.toLocalDate();

        // Get ALL punches for the day to determine last punch status
        List<WebPunch> allDayPunches = webPunchRepository
                .findByEmployeeIdAndPunchTimeBetweenOrderByPunchTimeAsc(
                        employeeId,
                        currentDate.atStartOfDay(),
                        currentDate.plusDays(1).atStartOfDay()
                );

        // Get last punch status regardless of shift time
        Optional<WebPunch> lastPunchStatus = allDayPunches.stream()
                .reduce((first, second) -> second);

        // Get current roster
        ShiftRosterDetails activeRoster = shiftRosterDetailsRepository
                .findByEmployeeIdAndShiftDateAndRecordStatus(employeeId, currentDate, EnumRecordStatus.ACTIVE)
                .orElse(null);

        if (activeRoster == null) {
            return TimeCalculationResponse.builder()
                    .lastPunch(String.valueOf(lastPunchStatus.map(WebPunch::getPunchType).orElse(null)))
                    .build();
        }

        ShiftDetails shift = activeRoster.getShiftDetails();
        LocalDateTime shiftStart = activeRoster.getShiftDate().atTime(shift.getStartTime());
        LocalDateTime shiftEnd = shift.getStartTime().isBefore(shift.getEndTime()) ?
                activeRoster.getShiftDate().atTime(shift.getEndTime()) :
                activeRoster.getShiftDate().plusDays(1).atTime(shift.getEndTime());

        // Get shift punches for calculations
        List<WebPunch> shiftPunches = webPunchRepository
                .findByEmployeeIdAndPunchTimeBetweenOrderByPunchTimeAsc(
                        employeeId, shiftStart, shiftEnd);

        Optional<WebPunch> firstPunchIn = shiftPunches.stream()
                .filter(p -> EnumPunchType.IN.equals(p.getPunchType()))
                .findFirst();

        Optional<WebPunch> lastPunchOut = shiftPunches.stream()
                .filter(p -> EnumPunchType.OUT.equals(p.getPunchType()))
                .reduce((first, second) -> second);

        Duration totalHours = (firstPunchIn.isPresent() && lastPunchOut.isPresent()) ?
                Duration.between(firstPunchIn.get().getPunchTime(), lastPunchOut.get().getPunchTime()) : Duration.ZERO;

        return punchMapper.toTimeCalculationResponse(
                firstPunchIn,
                lastPunchOut,
                totalHours,
                lastPunchStatus,
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



