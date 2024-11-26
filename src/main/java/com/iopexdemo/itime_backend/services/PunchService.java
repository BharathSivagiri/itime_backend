package com.iopexdemo.itime_backend.services;

import com.iopexdemo.itime_backend.dto.PunchRequest;
import com.iopexdemo.itime_backend.dto.TimeCalculationResponse;

import java.time.LocalDate;

public interface PunchService {

    void recordPunch(PunchRequest request);

    TimeCalculationResponse calculateTime(Integer employeeId, LocalDate date);
}
