package com.iopexdemo.itime_backend.services.implementations;

import com.iopexdemo.itime_backend.dto.*;
import com.iopexdemo.itime_backend.entities.ShiftDetails;
import com.iopexdemo.itime_backend.entities.ShiftRosterDetails;
import com.iopexdemo.itime_backend.entities.WebPunch;
import com.iopexdemo.itime_backend.enums.EnumPunchType;
import com.iopexdemo.itime_backend.mapper.PunchMapper;
import com.iopexdemo.itime_backend.repositories.EmployeeRepository;
import com.iopexdemo.itime_backend.repositories.ShiftRosterDetailsRepository;
import com.iopexdemo.itime_backend.repositories.WebPunchRepository;
import com.iopexdemo.itime_backend.services.PunchService;
import com.iopexdemo.itime_backend.validators.PunchValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final ShiftRosterDetailsRepository shiftRosterDetailsRepository;

    public void recordPunch(PunchRequest request) {
        logger.info("Validation for employee data and punch request in database started.");
        var employee = punchValidator.getValidatedEmployee(request.getEmployeeId(), employeeRepository);

        punchValidator.validateShiftAndPunchLimit(request.getEmployeeId(), request.getPunchType());

        WebPunch punch = punchMapper.toPunchEntity(request, employee);
        logger.info("Data saved in database after mapped using mapper class.");
        webPunchRepository.save(punch);
    }
    @Override
    public TimeCalculationResponse calculateTime(Integer employeeId, LocalDateTime targetDateTime) {
        LocalDateTime currentDateTime = targetDateTime != null ? targetDateTime : LocalDateTime.now();

        // Get the roster for current date
        ShiftRosterDetails rosterDetails = shiftRosterDetailsRepository
                .findByEmployeeIdAndShiftDate(employeeId, currentDateTime.toLocalDate());

        if (rosterDetails == null) {
            return TimeCalculationResponse.builder().build();
        }

        ShiftDetails shiftDetails = rosterDetails.getShiftDetails();
        LocalDate shiftDate = rosterDetails.getShiftDate();

        // Calculate query window based on shift type
        LocalDateTime queryStart;
        LocalDateTime queryEnd;

        if (shiftDetails.getStartTime().isBefore(shiftDetails.getEndTime())) {
            // Day shift
            queryStart = LocalDateTime.of(shiftDate, shiftDetails.getStartTime());
            queryEnd = LocalDateTime.of(shiftDate, shiftDetails.getEndTime());
        } else {
            // Night shift
            queryStart = LocalDateTime.of(shiftDate, shiftDetails.getStartTime());
            queryEnd = LocalDateTime.of(shiftDate.plusDays(1), shiftDetails.getEndTime());
        }

        List<WebPunch> punches = webPunchRepository
                .findByEmployeeIdAndPunchTimeBetweenOrderByPunchTimeAsc(
                        employeeId, queryStart, queryEnd);

        Optional<WebPunch> firstPunchIn = punches.stream()
                .filter(p -> EnumPunchType.IN.equals(p.getPunchType()))
                .findFirst();

        Optional<WebPunch> lastPunchOut = punches.stream()
                .filter(p -> EnumPunchType.OUT.equals(p.getPunchType()))
                .reduce((first, second) -> second);

        Optional<WebPunch> lastPunch = punches.stream()
                .reduce((first, second) -> second);

        Duration totalHours = Duration.ZERO;
        if (firstPunchIn.isPresent() && lastPunchOut.isPresent()) {
            totalHours = Duration.between(firstPunchIn.get().getPunchTime(), lastPunchOut.get().getPunchTime());
        }

        return punchMapper.toTimeCalculationResponse(firstPunchIn, lastPunchOut, totalHours, lastPunch, shiftDetails);
    }

//    public WeeklyStatsResponse calculateWeeklyStats(Integer employeeId, LocalDate startDate, LocalDate endDate) {
//        Duration totalActualHours = Duration.ZERO;
//        Duration totalShiftHours = Duration.ZERO;
//        LocalDate currentDate = startDate;
//
//        while (!currentDate.isAfter(endDate)) {
//            ShiftRosterDetails rosterDetails = shiftRosterDetailsRepository
//                    .findByEmployeeIdAndShiftDate(employeeId, currentDate);
//
//            if (rosterDetails != null) {
//                ShiftDetails shiftDetails = rosterDetails.getShiftDetails();
//
//                // Calculate shift hours
//                Duration shiftHours = Duration.between(
//                        shiftDetails.getStartTime(),
//                        shiftDetails.getStartTime().isAfter(shiftDetails.getEndTime()) ?
//                                shiftDetails.getEndTime().plusHours(24) :
//                                shiftDetails.getEndTime()
//                );
//                totalShiftHours = totalShiftHours.plus(shiftHours);
//
//                // Get punch data
//                List<WebPunch> currentDayPunches = webPunchRepository.findByEmployeeIdAndPunchDateBetween(
//                        employeeId,
//                        currentDate,
//                        shiftDetails.getStartTime().isAfter(shiftDetails.getEndTime()) ?
//                                currentDate.plusDays(1) : currentDate
//                );
//
//                // Calculate actual hours from punches
//                if (!currentDayPunches.isEmpty()) {
//                    Optional<WebPunch> firstPunchIn = currentDayPunches.stream()
//                            .filter(p -> EnumPunchType.IN.equals(p.getPunchType()))
//                            .findFirst();
//
//                    Optional<WebPunch> lastPunchOut = currentDayPunches.stream()
//                            .filter(p -> EnumPunchType.OUT.equals(p.getPunchType()))
//                            .reduce((first, second) -> second);
//
//                    if (firstPunchIn.isPresent() && lastPunchOut.isPresent()) {
//                        Duration actualHours = Duration.between(
//                                firstPunchIn.get().getPunchTime(),
//                                lastPunchOut.get().getPunchTime()
//                        );
//                        totalActualHours = totalActualHours.plus(actualHours);
//                    }
//                }
//            }
//            currentDate = currentDate.plusDays(1);
//        }
//
//        return new WeeklyStatsResponse(startDate, endDate, totalShiftHours, totalActualHours);
//    }


}



