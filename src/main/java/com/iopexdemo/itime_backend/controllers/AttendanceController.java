package com.iopexdemo.itime_backend.controllers;

import com.iopexdemo.itime_backend.entities.Attendance;
import com.iopexdemo.itime_backend.services.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    AttendanceService attendanceService;

    @PostMapping("/punch-in")
    public ResponseEntity<Attendance> punchIn(@RequestParam int employeeId) {
        Attendance attendance = attendanceService.punchIn(employeeId);
        return ResponseEntity.ok(attendance);
    }

    @PostMapping("/punch-out")
    public ResponseEntity<Attendance> punchOut(@RequestParam int employeeId) {
        Attendance attendance = attendanceService.punchOut(employeeId);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/weekly-hours")
    public ResponseEntity<Double> getWeeklyHours(@RequestParam int employeeId) {
        Double weeklyHours = attendanceService.getWeeklyHours(employeeId);
        return ResponseEntity.ok(weeklyHours);
    }
}
