package com.iopexdemo.itime_backend.controllers;

import com.iopexdemo.itime_backend.dto.PunchRequest;
import com.iopexdemo.itime_backend.dto.TimeCalculationResponse;
import com.iopexdemo.itime_backend.dto.WeeklyStatsResponse;
import com.iopexdemo.itime_backend.services.implementations.PunchServiceImpl;
import com.iopexdemo.itime_backend.utilities.constants.AppMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/punch")
public class PunchController {

    private static final Logger logger = LoggerFactory.getLogger(PunchController.class);

    @Autowired
    PunchServiceImpl punchService;

    @PostMapping
    public ResponseEntity<String> punch(@Valid @RequestBody PunchRequest request) {
        logger.info("Request for web punch incoming in controller.");
        punchService.recordPunch(request);
        logger.info("Punch details saved successfully in the database.");
        return ResponseEntity.ok(AppMessages.SUCCESSFUL_MESSAGE);
    }

    @GetMapping("/calculate")
    public ResponseEntity<TimeCalculationResponse> calculateTime(
            HttpServletRequest request,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime targetDateTime) {
        Integer employeeId = (Integer) request.getAttribute("employeeId");
        logger.info("Time calculation for web punch data started.");
        logger.info("Time calculation completed.");
        return ResponseEntity.ok(punchService.calculateTime(employeeId, targetDateTime));
    }

    @GetMapping("/weekly-stats")
    public ResponseEntity<WeeklyStatsResponse> getWeeklyStats(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Integer employeeId = (Integer) request.getAttribute("employeeId");
        return ResponseEntity.ok(punchService.calculateWeeklyStats(employeeId, startDate, endDate));
    }
}

