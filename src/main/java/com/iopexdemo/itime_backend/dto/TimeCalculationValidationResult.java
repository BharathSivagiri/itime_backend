package com.iopexdemo.itime_backend.dto;

import com.iopexdemo.itime_backend.entities.ShiftRosterDetails;
import com.iopexdemo.itime_backend.entities.WebPunch;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TimeCalculationValidationResult {
    private List<WebPunch> validPunches;
    private ShiftRosterDetails shift;
}
