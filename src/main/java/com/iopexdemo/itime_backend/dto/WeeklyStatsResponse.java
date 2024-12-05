package com.iopexdemo.itime_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyStatsResponse {
    private String weekStartDate;
    private String weekEndDate;
    private String totalShiftHours;
    private String totalActualHours;
}
