package com.iopexdemo.itime_backend.dto;

import com.iopexdemo.itime_backend.enums.EnumPunchType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PunchRequest {
    private Integer employeeId;
    private EnumPunchType punchType;
}

