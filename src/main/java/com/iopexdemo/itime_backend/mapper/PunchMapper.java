package com.iopexdemo.itime_backend.mapper;

import com.iopexdemo.itime_backend.dto.PunchRequest;
import com.iopexdemo.itime_backend.dto.TimeCalculationResponse;
import com.iopexdemo.itime_backend.entities.EmployeeDetails;
import com.iopexdemo.itime_backend.entities.ShiftDetails;
import com.iopexdemo.itime_backend.entities.WebPunch;
import com.iopexdemo.itime_backend.enums.EnumPunchType;
import com.iopexdemo.itime_backend.enums.EnumStatus;
import com.iopexdemo.itime_backend.utilities.DateTimeUtil;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PunchMapper {

    public WebPunch toPunchEntity(PunchRequest request, EmployeeDetails employee) {
        return WebPunch.builder()
                .employee(employee)
                .punchTime(LocalDateTime.now())
                .punchType(EnumPunchType.valueOf(String.valueOf(request.getPunchType())))
                .status(EnumStatus.ACTIVE)
                .createdBy(employee.getEmpCode())
                .createdDt(LocalDate.now())
                .updatedBy(employee.getEmpCode())
                .updatedDt(LocalDate.now())
                .build();
    }

    public TimeCalculationResponse toTimeCalculationResponse(
            Optional<WebPunch> lastPunchIn,
            Optional<WebPunch> lastPunchOut,
            Duration totalHours,
            Optional<WebPunch> lastPunch,
            ShiftDetails shiftDetails)

    {
        return TimeCalculationResponse.builder()
                .punchInTime(lastPunchIn.map(WebPunch::getPunchTime).orElse(null))
                .punchOutTime(lastPunchOut.map(WebPunch::getPunchTime).orElse(null))
                .totalWorkingHours(DateTimeUtil.formatDuration(totalHours))
                .lastPunch(lastPunch.map(punch -> punch.getPunchType().toString()).orElse(null))
                .shiftStartTime(String.valueOf(shiftDetails.getStartTime()))
                .shiftEndTime(String.valueOf(shiftDetails.getEndTime()))
                .build();
    }
}
