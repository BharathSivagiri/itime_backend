package com.iopexdemo.itime_backend.services.implementations;

import com.iopexdemo.itime_backend.dto.PunchRequest;
import com.iopexdemo.itime_backend.dto.TimeCalculationResponse;
import com.iopexdemo.itime_backend.entities.WebPunch;
import com.iopexdemo.itime_backend.enums.EnumPunchType;
import com.iopexdemo.itime_backend.enums.EnumStatus;
import com.iopexdemo.itime_backend.mapper.PunchMapper;
import com.iopexdemo.itime_backend.repositories.EmployeeRepository;
import com.iopexdemo.itime_backend.repositories.WebPunchRepository;
import com.iopexdemo.itime_backend.services.PunchService;
import com.iopexdemo.itime_backend.validators.PunchValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
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

    public TimeCalculationResponse calculateTime(Integer employeeId) {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        logger.info("Punch data of the day is retrieved and calculated.");
        // Fetch all punches for the employee for the current day
        List<WebPunch> punches = webPunchRepository
                .findByEmployeeIdAndStatusAndPunchTimeBetweenOrderByPunchTimeAsc(
                        employeeId, EnumStatus.ACTIVE, today, today.plusDays(1));

        // Get the first punch-in of the day
        Optional<WebPunch> firstPunchIn = punches.stream()
                .filter(p -> EnumPunchType.IN.equals(p.getPunchType()))
                .findFirst(); // Use findFirst to get the earliest punch-in

        // Get the last punch-out of the day
        Optional<WebPunch> lastPunchOut = punches.stream()
                .filter(p -> EnumPunchType.OUT.equals(p.getPunchType()))
                .reduce((first, second) -> second);

        // Calculate the total working hours
        Duration totalHours = Duration.ZERO;
        if (firstPunchIn.isPresent() && lastPunchOut.isPresent()) {
            totalHours = Duration.between(firstPunchIn.get().getPunchTime(), lastPunchOut.get().getPunchTime());
        }

        logger.info("Response data is generated using mapper class.");
        // Use the mapper to generate the response
        return punchMapper.toTimeCalculationResponse(firstPunchIn, lastPunchOut, totalHours);
    }

}
