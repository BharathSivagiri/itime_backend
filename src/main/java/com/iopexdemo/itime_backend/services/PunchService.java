package com.iopexdemo.itime_backend.services;

import com.iopexdemo.itime_backend.dto.PunchRequest;
import com.iopexdemo.itime_backend.dto.TimeCalculationResponse;
import com.iopexdemo.itime_backend.dto.WeeklyStatsResponse;

import java.time.LocalDateTime;

public interface PunchService {

    void recordPunch(PunchRequest request);

    TimeCalculationResponse calculateTime(Integer employeeId, LocalDateTime date);

//    WeeklyStatsResponse calculateWeeklyStats(Integer employeeId, LocalDate startDate, LocalDate endDate);
}
