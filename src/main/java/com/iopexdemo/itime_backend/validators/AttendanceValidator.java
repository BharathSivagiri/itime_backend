package com.iopexdemo.itime_backend.validators;

import com.iopexdemo.itime_backend.entities.Attendance;
import com.iopexdemo.itime_backend.exceptions.custom.AttendanceException;
import com.iopexdemo.itime_backend.repositories.AttendanceRepository;
import com.iopexdemo.itime_backend.utilities.constants.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AttendanceValidator {

    @Autowired
    AttendanceRepository attendanceRepository;

    public void validateActivePunchIn(int employeeId) throws AttendanceException {
        if (attendanceRepository.findFirstByEmployeeIdAndPunchOutTimeIsNullAndRecStatusOrderByPunchInTimeDesc(
                employeeId, "ACTIVE").isPresent()) {
            throw new AttendanceException(ErrorMessages.ALREADY_PUNCHED_IN);
        }
    }

    public Attendance validateActivePunchOut(int employeeId) {
        return attendanceRepository.findFirstByEmployeeIdAndPunchOutTimeIsNullAndRecStatusOrderByPunchInTimeDesc(
                        employeeId, "ACTIVE")
                .orElseThrow(() -> new AttendanceException(ErrorMessages.PUNCH_RECORD_NOT_FOUND));
    }
}
