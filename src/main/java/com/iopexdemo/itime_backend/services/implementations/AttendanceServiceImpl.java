package com.iopexdemo.itime_backend.services.implementations;

import com.iopexdemo.itime_backend.entities.Attendance;
import com.iopexdemo.itime_backend.mapper.AttendanceMapper;
import com.iopexdemo.itime_backend.repositories.AttendanceRepository;
import com.iopexdemo.itime_backend.services.AttendanceService;
import com.iopexdemo.itime_backend.validators.AttendanceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    AttendanceValidator attendanceValidator;

    @Autowired
    AttendanceMapper attendanceMapper;

    public Attendance punchIn(int employeeId) {
        attendanceValidator.validateActivePunchIn(employeeId);

        Attendance attendance = attendanceMapper.createPunchInEntity(employeeId);
        return attendanceRepository.save(attendance);
    }

    public Attendance punchOut(int employeeId) {
        Attendance attendance = attendanceValidator.validateActivePunchOut(employeeId);

        LocalDateTime punchOutTime = LocalDateTime.now();
        attendance = attendanceMapper.updatePunchOutEntity(attendance, employeeId, punchOutTime);
        return attendanceRepository.save(attendance);
    }

    public Double getWeeklyHours(int employeeId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);

        return attendanceRepository.sumTotalHoursByEmployeeIdAndPunchInTimeBetweenAndRecStatus(
                employeeId, startOfWeek, endOfWeek, "ACTIVE");
    }
}
