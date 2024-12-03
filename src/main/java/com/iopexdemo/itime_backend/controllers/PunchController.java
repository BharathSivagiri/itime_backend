package com.iopexdemo.itime_backend.controllers;

import com.iopexdemo.itime_backend.dto.PunchRequest;
import com.iopexdemo.itime_backend.dto.TimeCalculationResponse;
import com.iopexdemo.itime_backend.dto.WeeklyStatsResponse;
import com.iopexdemo.itime_backend.services.implementations.PunchServiceImpl;
import com.iopexdemo.itime_backend.utilities.constants.AppMessages;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/punch")
@CrossOrigin
@RequiredArgsConstructor
public class PunchController {

    private static final Logger logger = LoggerFactory.getLogger(PunchController.class);

    @Autowired
    PunchServiceImpl punchService;

    @PostMapping
    public ResponseEntity<String> punch(@RequestBody PunchRequest request) {
        logger.info("Request for web punch incoming in controller.");
        punchService.recordPunch(request);
        logger.info("Punch details saved successfully in the database.");
        return ResponseEntity.ok(AppMessages.SUCCESSFUL_MESSAGE);
    }

    @GetMapping("/calculate/{employeeId}")
    public ResponseEntity<TimeCalculationResponse> calculateTime(
            @PathVariable Integer employeeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime targetDateTime) {
        logger.info("Time calculation for web punch data started.");
        TimeCalculationResponse timeResponse = punchService.calculateTime(employeeId, targetDateTime);
        logger.info("Time calculation completed.");
        return ResponseEntity.ok(timeResponse);
    }

    @GetMapping("/weekly-stats")
    public ResponseEntity<WeeklyStatsResponse> getWeeklyStats(
            @RequestParam Integer employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        WeeklyStatsResponse stats = punchService.calculateWeeklyStats(employeeId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }
}

