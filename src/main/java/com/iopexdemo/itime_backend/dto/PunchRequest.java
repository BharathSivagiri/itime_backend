package com.iopexdemo.itime_backend.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PunchRequest {

    @Pattern(regexp = "^[0-9]+$", message = "Input must contain only numbers")
    private String employeeId;

    @Pattern(regexp = "^(IN|OUT)$", message = "Input must be only IN or OUT")
    private String punchType;
}

