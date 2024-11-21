package com.iopexdemo.itime_backend.services;

import com.iopexdemo.itime_backend.entities.Attendance;

public interface AttendanceService {

    Attendance punchIn(int employeeId);

    Attendance punchOut(int employeeId);

    Double getWeeklyHours(int employeeId);
}
