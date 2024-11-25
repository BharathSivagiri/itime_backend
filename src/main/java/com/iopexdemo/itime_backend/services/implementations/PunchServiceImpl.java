package com.iopexdemo.itime_backend.services.implementations;

import com.iopexdemo.itime_backend.dto.PunchRequest;
import com.iopexdemo.itime_backend.dto.TimeCalculationResponse;
import com.iopexdemo.itime_backend.entities.WebPunch;
import com.iopexdemo.itime_backend.enums.EnumPunchType;
import com.iopexdemo.itime_backend.mapper.PunchMapper;
import com.iopexdemo.itime_backend.repositories.EmployeeRepository;
import com.iopexdemo.itime_backend.repositories.WebPunchRepository;
import com.iopexdemo.itime_backend.services.PunchService;
import com.iopexdemo.itime_backend.validators.PunchValidator;
import com.iopexdemo.itime_backend.dto.TimeCalculationValidationResult;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PunchServiceImpl implements PunchService {

    private static final Logger logger = LoggerFactory.getLogger(PunchServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final WebPunchRepository webPunchRepository;
    private final PunchMapper punchMapper;
    private final PunchValidator punchValidator;

    public void recordPunch(PunchRequest request) {
        logger.info("Validation for employee data and punch request in database started.");
        var employee = punchValidator.getValidatedEmployee(request.getEmployeeId(), employeeRepository);

        punchValidator.validatePunchRequest(request, employee);

        WebPunch punch = punchMapper.toPunchEntity(request, employee);

        logger.info("Data saved in database after mapped using mapper class.");
        webPunchRepository.save(punch);
    }

    @Override
    public TimeCalculationResponse calculateTime(Integer employeeId) {
        // Get the current date automatically
        LocalDate currentDate = LocalDate.now();

        // Validate the punches for the given employee and date
        TimeCalculationValidationResult validationResult = punchValidator.validateTimeCalculation(employeeId, currentDate);
        List<WebPunch> punches = validationResult.getValidPunches();

        // Find the first "IN" punch and the last "OUT" punch
        Optional<WebPunch> firstPunchIn = punches.stream()
                .filter(p -> EnumPunchType.IN.equals(p.getPunchType()))
                .findFirst();

        Optional<WebPunch> lastPunch = punches.stream()
                .reduce((first, second) -> second);

        Optional<WebPunch> lastPunchOut = punches.stream()
                .filter(p -> EnumPunchType.OUT.equals(p.getPunchType()))
                .reduce((first, second) -> second);

        // Calculate the total hours worked between the first IN punch and last OUT punch
        Duration totalHours = Duration.ZERO;
        if (firstPunchIn.isPresent() && lastPunchOut.isPresent()) {
            totalHours = Duration.between(firstPunchIn.get().getPunchTime(), lastPunchOut.get().getPunchTime());
        }

        // Return the time calculation response
        return punchMapper.toTimeCalculationResponse(firstPunchIn, lastPunchOut, totalHours, lastPunch);
    }

}



