package com.iopexdemo.itime_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class TimeCalculationResponse {
    private LocalDateTime punchInTime;
    private LocalDateTime punchOutTime;
    private String totalWorkingHours;
    private String lastPunch;
    private LocalTime shiftStartTime;
    private LocalTime shiftEndTime;
}

