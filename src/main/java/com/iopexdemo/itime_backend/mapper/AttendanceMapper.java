package com.iopexdemo.itime_backend.mapper;

import com.iopexdemo.itime_backend.entities.Attendance;
import com.iopexdemo.itime_backend.enums.DBRecordStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AttendanceMapper {

    public Attendance createPunchInEntity(int employeeId) {
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setPunchInTime(LocalDateTime.now());
        attendance.setRecStatus(DBRecordStatus.ACTIVE);
        attendance.setCreatedDate(LocalDateTime.now().toString());
        attendance.setCreatedBy(String.valueOf(employeeId));
        return attendance;
    }

    public Attendance updatePunchOutEntity(Attendance attendance, int employeeId, LocalDateTime punchOutTime) {
        attendance.setPunchOutTime(punchOutTime);
        double hours = java.time.temporal.ChronoUnit.MINUTES.between(attendance.getPunchInTime(), punchOutTime) / 60.0;
        attendance.setTotalHours(hours);
        attendance.setUpdatedDate(LocalDateTime.now().toString());
        attendance.setUpdatedBy(String.valueOf(employeeId));
        return attendance;
    }
}
