package com.iopexdemo.itime_backend.repositories;

import com.iopexdemo.itime_backend.entities.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    Optional<Attendance> findFirstByEmployeeIdAndPunchOutTimeIsNullAndRecStatusOrderByPunchInTimeDesc(
            int employeeId, String recStatus);

    Double sumTotalHoursByEmployeeIdAndPunchInTimeBetweenAndRecStatus(
            int employeeId, LocalDateTime startDate, LocalDateTime endDate, String recStatus);
}
