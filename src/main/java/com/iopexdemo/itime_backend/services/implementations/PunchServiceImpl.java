package com.iopexdemo.itime_backend.services.implementations;

import com.iopexdemo.itime_backend.dto.*;
import com.iopexdemo.itime_backend.entities.EmployeeDetails;
import com.iopexdemo.itime_backend.entities.ShiftDetails;
import com.iopexdemo.itime_backend.entities.ShiftRosterDetails;
import com.iopexdemo.itime_backend.entities.WebPunch;
import com.iopexdemo.itime_backend.enums.EnumDayType;
import com.iopexdemo.itime_backend.enums.EnumPunchType;
import com.iopexdemo.itime_backend.enums.EnumRecordStatus;
import com.iopexdemo.itime_backend.mapper.PunchMapper;
import com.iopexdemo.itime_backend.repositories.ShiftRosterDetailsRepository;
import com.iopexdemo.itime_backend.repositories.WebPunchRepository;
import com.iopexdemo.itime_backend.services.PunchService;
import com.iopexdemo.itime_backend.validators.PunchValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    WebPunchRepository webPunchRepository;

    @Autowired
    PunchMapper punchMapper;

    @Autowired
    PunchValidator punchValidator;

    @Autowired
    ShiftRosterDetailsRepository shiftRosterDetailsRepository;

    @Override
    public void recordPunch(PunchRequest request) {
        logger.info("Validating employee and punch data...");
        EmployeeDetails employee = punchValidator.getValidatedEmployee(request.getEmployeeId());

        // Get current roster and check for night shift
        LocalDate currentDate = LocalDate.now();
        ShiftRosterDetails currentRoster = getRosterDetails(request.getEmployeeId(), currentDate);
        ShiftRosterDetails previousDayRoster = getRosterDetails(request.getEmployeeId(), currentDate.minusDays(1));

        boolean isNightShift = isNightShift(LocalDateTime.now(), previousDayRoster);

        // Validate punch based on day type and shift
        if (shouldApplyPunchLimit(currentRoster, isNightShift)) {
            punchValidator.validateShiftAndPunchLimit(request.getEmployeeId(), request.getPunchType());
        }

        WebPunch punch = punchMapper.toPunchEntity(request, employee);
        webPunchRepository.save(punch);
        logger.info("Punch recorded successfully.");
    }

    @Override
    public TimeCalculationResponse calculateTime(Integer employeeId, LocalDateTime targetDateTime) {
        LocalDateTime currentDateTime = Optional.ofNullable(targetDateTime).orElse(LocalDateTime.now());
        LocalDate currentDate = currentDateTime.toLocalDate();

        Optional<WebPunch> lastPunchStatus = getLastPunchStatus(employeeId, currentDate);
        ShiftRosterDetails previousDayRoster = getRosterDetails(employeeId, currentDate.minusDays(1));
        ShiftRosterDetails currentRoster = getRosterDetails(employeeId, currentDate);

        // Handle night shift scenario
        if (isNightShift(currentDateTime, previousDayRoster)) {
            return calculateShiftTime(employeeId, previousDayRoster, currentDateTime, lastPunchStatus);
        }

        // Check if it's a valid punch day
        if (!isValidPunchDay(currentRoster)) {
            return buildBasicResponse(lastPunchStatus);
        }

        return calculateShiftTime(employeeId, currentRoster, currentDateTime, lastPunchStatus);
    }

    @Override
    public WeeklyStatsResponse calculateWeeklyStats(Integer employeeId, LocalDate startDate, LocalDate endDate) {
        Duration totalActualHours = Duration.ZERO;
        Duration totalShiftHours = Duration.ZERO;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            ShiftRosterDetails roster = getRosterDetails(employeeId, date);

            // Only calculate hours for valid punch days
            if (isValidPunchDay(roster)) {
                Duration shiftHours = calculateShiftDuration(roster);
                totalShiftHours = totalShiftHours.plus(shiftHours);

                Duration actualHours = calculateActualHours(employeeId, roster);
                totalActualHours = totalActualHours.plus(actualHours);
            }
        }

        return buildWeeklyStatsResponse(startDate, endDate, totalShiftHours, totalActualHours);
    }

    // Helper Methods

    private boolean shouldApplyPunchLimit(ShiftRosterDetails roster, boolean isNightShift) {
        if (roster == null) return false;

        EnumDayType dayType = roster.getDayType();

        // For REGULAR days, always apply punch limit
        if (dayType == EnumDayType.REGULAR) return true;

        // For WEEK_OFF and HOLIDAY, only apply punch limit if there's a night shift
        return (dayType == EnumDayType.WEEK_OFF || dayType == EnumDayType.HOLIDAY) && isNightShift;
    }

    private boolean isValidPunchDay(ShiftRosterDetails roster) {
        return roster != null &&
                (roster.getDayType() == EnumDayType.REGULAR ||
                        roster.getShiftDetails() != null);
    }

    private Optional<WebPunch> getLastPunchStatus(Integer employeeId, LocalDate currentDate) {
        List<WebPunch> punches = webPunchRepository.findByEmployeeIdAndPunchTimeBetweenOrderByPunchTimeAsc(
                employeeId, currentDate.atStartOfDay(), currentDate.plusDays(1).atStartOfDay()
        );
        return punches.stream().reduce((first, second) -> second);
    }

    private ShiftRosterDetails getRosterDetails(Integer employeeId, LocalDate date) {
        return shiftRosterDetailsRepository.findByEmployeeIdAndShiftDateAndRecordStatus(
                employeeId, date, EnumRecordStatus.ACTIVE).orElse(null);
    }

    private boolean isNightShift(LocalDateTime currentDateTime, ShiftRosterDetails roster) {
        if (roster == null || roster.getShiftDetails() == null) return false;
        ShiftDetails shift = roster.getShiftDetails();
        return shift.getStartTime().isAfter(shift.getEndTime())
                && isWithinShift(currentDateTime, roster.getShiftDate().atTime(shift.getStartTime()),
                roster.getShiftDate().plusDays(1).atTime(shift.getEndTime()));
    }

    private boolean isWithinShift(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        return !dateTime.isBefore(start) && !dateTime.isAfter(end);
    }

    private TimeCalculationResponse calculateShiftTime(Integer employeeId, ShiftRosterDetails roster,
                                                       LocalDateTime currentDateTime, Optional<WebPunch> lastPunchStatus) {
        ShiftDetails shift = roster.getShiftDetails();
        LocalDateTime shiftStart = roster.getShiftDate().atTime(shift.getStartTime());
        LocalDateTime shiftEnd = shift.getStartTime().isBefore(shift.getEndTime())
                ? roster.getShiftDate().atTime(shift.getEndTime())
                : roster.getShiftDate().plusDays(1).atTime(shift.getEndTime());

        //Condition to check and build upcoming shifts time if there is an assigned shift
        if(currentDateTime.isBefore(shiftStart)){
            return TimeCalculationResponse.builder()
                    .lastPunch(String.valueOf(lastPunchStatus.map(WebPunch::getPunchType).orElse(null)))
                    .shiftStartTime(shift.getStartTime())
                    .shiftEndTime(shift.getEndTime())
                    .build();
        }

        if (!isWithinShift(currentDateTime, shiftStart, shiftEnd)) {
            return buildBasicResponse(lastPunchStatus);
        }

        List<WebPunch> punches = webPunchRepository.findByEmployeeIdAndPunchTimeBetweenOrderByPunchTimeAsc(
                employeeId, shiftStart, shiftEnd
        );

        Optional<WebPunch> firstPunchIn = punches.stream().filter(p -> EnumPunchType.IN.equals(p.getPunchType())).findFirst();
        Optional<WebPunch> lastPunchOut = punches.stream().filter(p -> EnumPunchType.OUT.equals(p.getPunchType())).reduce((a, b) -> b);

        Duration totalHours = (firstPunchIn.isPresent() && lastPunchOut.isPresent())
                ? Duration.between(firstPunchIn.get().getPunchTime(), lastPunchOut.get().getPunchTime())
                : Duration.ZERO;

        return punchMapper.toTimeCalculationResponse(firstPunchIn, lastPunchOut, totalHours, lastPunchStatus, shift);
    }

    private Duration calculateShiftDuration(ShiftRosterDetails roster) {
        ShiftDetails shift = roster.getShiftDetails();
        LocalDateTime shiftStart = roster.getShiftDate().atTime(shift.getStartTime());
        LocalDateTime shiftEnd = shift.getStartTime().isBefore(shift.getEndTime())
                ? roster.getShiftDate().atTime(shift.getEndTime())
                : roster.getShiftDate().plusDays(1).atTime(shift.getEndTime());
        return Duration.between(shiftStart, shiftEnd);
    }

    private Duration calculateActualHours(Integer employeeId, ShiftRosterDetails roster) {
        LocalDateTime shiftStart = roster.getShiftDate().atTime(roster.getShiftDetails().getStartTime());
        LocalDateTime shiftEnd = roster.getShiftDate().plusDays(1).atTime(roster.getShiftDetails().getEndTime());

        List<WebPunch> punches = webPunchRepository.findByEmployeeIdAndPunchTimeBetweenOrderByPunchTimeAsc(
                employeeId, shiftStart, shiftEnd
        );

        Optional<WebPunch> firstPunchIn = punches.stream().filter(p -> EnumPunchType.IN.equals(p.getPunchType())).findFirst();
        Optional<WebPunch> lastPunchOut = punches.stream().filter(p -> EnumPunchType.OUT.equals(p.getPunchType())).reduce((a, b) -> b);

        return firstPunchIn.isPresent() && lastPunchOut.isPresent()
                ? Duration.between(firstPunchIn.get().getPunchTime(), lastPunchOut.get().getPunchTime())
                : Duration.ZERO;
    }

    private TimeCalculationResponse buildBasicResponse(Optional<WebPunch> lastPunchStatus) {
        return TimeCalculationResponse.builder()
                .lastPunch(String.valueOf(lastPunchStatus.map(WebPunch::getPunchType).orElse(null)))
                .build();
    }

    private WeeklyStatsResponse buildWeeklyStatsResponse(LocalDate startDate, LocalDate endDate,
                                                         Duration totalShiftHours, Duration totalActualHours) {
        return new WeeklyStatsResponse(
                startDate, endDate,
                formatDuration(totalShiftHours),
                formatDuration(totalActualHours)
        );
    }

    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d", duration.toHours(), duration.toMinutesPart());
    }
}
