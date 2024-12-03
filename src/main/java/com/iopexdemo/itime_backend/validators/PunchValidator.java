package com.iopexdemo.itime_backend.validators;

import com.iopexdemo.itime_backend.entities.EmployeeDetails;
import com.iopexdemo.itime_backend.entities.ShiftDetails;
import com.iopexdemo.itime_backend.entities.ShiftRosterDetails;
import com.iopexdemo.itime_backend.entities.WebPunch;
import com.iopexdemo.itime_backend.enums.EnumEmployeeStatus;
import com.iopexdemo.itime_backend.enums.EnumPunchType;
import com.iopexdemo.itime_backend.enums.EnumRecordStatus;
import com.iopexdemo.itime_backend.exceptions.custom.CustomException;
import com.iopexdemo.itime_backend.repositories.EmployeeRepository;
import com.iopexdemo.itime_backend.repositories.ShiftRosterDetailsRepository;
import com.iopexdemo.itime_backend.repositories.WebPunchRepository;
import com.iopexdemo.itime_backend.utilities.constants.AppMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PunchValidator {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ShiftRosterDetailsRepository shiftRosterRepository;

    @Autowired
    WebPunchRepository webPunchRepository;

    @Value("${punch.limit.daily}")
    Integer dailyPunchLimit;

    public EmployeeDetails getValidatedEmployee(Integer employeeId) {
        return employeeRepository.findByIdAndEmpStatus(employeeId, EnumEmployeeStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(AppMessages.EMPLOYEE_NOT_FOUND));
    }
    public void validateShiftAndPunchLimit(Integer employeeId, EnumPunchType punchType) {
        LocalDateTime currentTime = LocalDateTime.now();

        // Get current shift roster
        ShiftRosterDetails roster = shiftRosterRepository
                .findByEmployeeIdAndShiftDateAndRecordStatus(
                        employeeId,
                        currentTime.toLocalDate(),
                        EnumRecordStatus.ACTIVE
                ).orElse(null);

        // Check previous day for night shifts
        if (roster == null) {
            roster = shiftRosterRepository
                    .findByEmployeeIdAndShiftDateAndRecordStatus(
                            employeeId,
                            currentTime.toLocalDate().minusDays(1),
                            EnumRecordStatus.ACTIVE
                    ).orElseThrow(() -> new CustomException(AppMessages.SHIFT_NOT_ASSIGNED));
        }

        ShiftDetails shift = roster.getShiftDetails();
        LocalDateTime shiftStart;
        LocalDateTime shiftEnd;

        if (shift.getStartTime().isBefore(shift.getEndTime())) {
            // Day shift
            shiftStart = roster.getShiftDate().atTime(shift.getStartTime());
            shiftEnd = roster.getShiftDate().atTime(shift.getEndTime());
        } else {
            // Night shift
            shiftStart = roster.getShiftDate().atTime(shift.getStartTime());
            shiftEnd = roster.getShiftDate().plusDays(1).atTime(shift.getEndTime());
        }

        // Only apply punch limit validation if within shift hours
        if (currentTime.isAfter(shiftStart) && currentTime.isBefore(shiftEnd)) {
            List<WebPunch> shiftPunches = webPunchRepository
                    .findByEmployeeIdAndPunchTimeBetweenOrderByPunchTimeAsc(
                            employeeId, shiftStart, shiftEnd);

            if (shiftPunches.size() >= dailyPunchLimit) {
                throw new CustomException(AppMessages.PUNCH_LIMIT_EXCEEDED);
            }
        }
        // Outside shift hours - punch is allowed without limit validation
    }

}


