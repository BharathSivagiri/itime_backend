package com.iopexdemo.itime_backend.validators;

import com.iopexdemo.itime_backend.dto.PunchRequest;
import com.iopexdemo.itime_backend.dto.TimeCalculationValidationResult;
import com.iopexdemo.itime_backend.entities.EmployeeDetails;
import com.iopexdemo.itime_backend.entities.ShiftRosterDetails;
import com.iopexdemo.itime_backend.entities.WebPunch;
import com.iopexdemo.itime_backend.enums.EnumEmployeeStatus;
import com.iopexdemo.itime_backend.enums.EnumRecordStatus;
import com.iopexdemo.itime_backend.enums.EnumStatus;
import com.iopexdemo.itime_backend.exceptions.custom.CustomException;
import com.iopexdemo.itime_backend.repositories.EmployeeRepository;
import com.iopexdemo.itime_backend.repositories.ShiftRosterRepository;
import com.iopexdemo.itime_backend.repositories.WebPunchRepository;
import com.iopexdemo.itime_backend.utilities.constants.AppMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PunchValidator {
    private final ShiftRosterRepository shiftRosterRepository;
    private final WebPunchRepository webPunchRepository;

    @Value("${punch.limit.daily}")
    private Integer dailyPunchLimit;

    public void validatePunchRequest(PunchRequest request, EmployeeDetails employee) {
        if (employee == null) {
            throw new CustomException(AppMessages.EMPLOYEE_NOT_FOUND);
        }
        if (!EnumEmployeeStatus.ACTIVE.equals(employee.getEmpStatus())) {
            throw new CustomException(AppMessages.EMPLOYEE_RECORD_NOT_FOUND);
        }

        LocalDate punchDate = LocalDate.now();
        validateShiftTiming(request, punchDate);
        validateDailyPunchLimit(request, punchDate);
    }

    private void validateShiftTiming(PunchRequest request, LocalDate punchDate) {
        LocalTime currentTime = LocalTime.now();

        ShiftRosterDetails roster = shiftRosterRepository
                .findByEmployeeIdAndShiftDateAndRecordStatus(
                        request.getEmployeeId(),
                        punchDate,
                        EnumRecordStatus.ACTIVE
                )
                .orElseThrow(() -> new CustomException("No shift assigned for this date"));

        // Check if the shift crosses midnight (start time > end time)
        if (roster.getShiftDetails().getStartTime().isAfter(roster.getShiftDetails().getEndTime())) {
            if (currentTime.isBefore(roster.getShiftDetails().getStartTime()) &&
                    currentTime.isAfter(roster.getShiftDetails().getEndTime())) {
                throw new CustomException("Current time is outside assigned shift hours");
            }
        } else {
            // Regular shift within the same day
            if (currentTime.isBefore(roster.getShiftDetails().getStartTime()) ||
                    currentTime.isAfter(roster.getShiftDetails().getEndTime())) {
                throw new CustomException("Current time is outside assigned shift hours");
            }
        }
    }

    private void validateDailyPunchLimit(PunchRequest request, LocalDate punchDate) {
        ShiftRosterDetails roster = shiftRosterRepository
                .findByEmployeeIdAndShiftDateAndRecordStatus(
                        request.getEmployeeId(),
                        punchDate,
                        EnumRecordStatus.ACTIVE
                )
                .orElseThrow(() -> new CustomException("No shift assigned for this date"));

        LocalDateTime startOfDay = punchDate.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        if (roster.getShiftDetails().getStartTime().isAfter(roster.getShiftDetails().getEndTime())) {

            LocalDateTime startOfShift = punchDate.atTime(roster.getShiftDetails().getStartTime());
            LocalDateTime midnight = punchDate.atTime(23, 59, 59);

            long punchCountBeforeMidnight = webPunchRepository.countByEmployeeIdAndPunchTimeBetween(
                    request.getEmployeeId(),
                    startOfShift,
                    midnight
            );

            LocalDateTime nextDayStart = punchDate.plusDays(1).atStartOfDay();
            LocalDateTime endOfShift = punchDate.plusDays(1).atTime(roster.getShiftDetails().getEndTime());

            long punchCountAfterMidnight = webPunchRepository.countByEmployeeIdAndPunchTimeBetween(
                    request.getEmployeeId(),
                    nextDayStart,
                    endOfShift
            );

            long totalPunchCount = punchCountBeforeMidnight + punchCountAfterMidnight;

            if (totalPunchCount >= dailyPunchLimit) {
                throw new CustomException("Daily punch limit exceeded");
            }

        } else {
            long punchCount = webPunchRepository.countByEmployeeIdAndPunchTimeBetween(
                    request.getEmployeeId(),
                    startOfDay,
                    endOfDay
            );

            if (punchCount >= dailyPunchLimit) {
                throw new CustomException("Daily punch limit exceeded");
            }
        }
    }

    public EmployeeDetails getValidatedEmployee(Integer employeeId, EmployeeRepository employeeRepository) {
        return employeeRepository.findByIdAndEmpStatus(employeeId, EnumEmployeeStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(AppMessages.EMPLOYEE_NOT_FOUND));
    }

    public TimeCalculationValidationResult validateTimeCalculation(Integer employeeId, LocalDate currentDate) {
        ShiftRosterDetails todayShift = shiftRosterRepository
                .findByEmployeeIdAndShiftDateAndRecordStatus(
                        employeeId,
                        currentDate,
                        EnumRecordStatus.ACTIVE
                )
                .orElseThrow(() -> new CustomException("No shift assigned for today"));

        List<WebPunch> punches = webPunchRepository
                .findByEmployeeIdAndStatusAndPunchTimeBetweenOrderByPunchTimeAsc(
                        employeeId,
                        EnumStatus.ACTIVE,
                        currentDate.atStartOfDay(),  // Start of the day
                        currentDate.plusDays(1).atStartOfDay()  // Start of the next day
                )
                .stream()
                .filter(punch -> isWithinShiftHours(punch, todayShift))  // Filter punches within shift hours
                .toList();

        return new TimeCalculationValidationResult(punches, todayShift);
    }

    private boolean isWithinShiftHours(WebPunch punch, ShiftRosterDetails shift) {
        LocalTime punchTime = punch.getPunchTime().toLocalTime();

        if (shift.getShiftDetails().getStartTime().isAfter(shift.getShiftDetails().getEndTime())) {
            return (punchTime.isAfter(shift.getShiftDetails().getStartTime()) || punchTime.isBefore(shift.getShiftDetails().getEndTime()));
        } else {
            return !punchTime.isBefore(shift.getShiftDetails().getStartTime()) &&
                    !punchTime.isAfter(shift.getShiftDetails().getEndTime());
        }
    }

}
