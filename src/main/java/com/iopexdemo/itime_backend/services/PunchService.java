package com.iopexdemo.itime_backend.services;

import com.iopexdemo.itime_backend.dto.PunchRequest;
import com.iopexdemo.itime_backend.dto.TimeCalculationResponse;

public interface PunchService {

    void recordPunch(PunchRequest request);

    TimeCalculationResponse calculateTime(Integer employeeId);

}
