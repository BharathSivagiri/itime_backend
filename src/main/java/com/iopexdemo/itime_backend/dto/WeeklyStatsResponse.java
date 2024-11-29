package com.iopexdemo.itime_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class WeeklyStatsResponse {
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private String totalShiftHours;
    private String totalActualHours;
}
