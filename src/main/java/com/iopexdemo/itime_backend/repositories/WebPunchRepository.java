package com.iopexdemo.itime_backend.repositories;

import com.iopexdemo.itime_backend.entities.WebPunch;
import com.iopexdemo.itime_backend.enums.EnumStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WebPunchRepository extends JpaRepository<WebPunch, Integer> {

    List<WebPunch> findByEmployeeIdAndStatusAndPunchTimeBetweenOrderByPunchTimeAsc(
            Integer employeeId,
            EnumStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime);

    long countByEmployeeIdAndPunchTimeBetween(
            Integer employeeId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}
