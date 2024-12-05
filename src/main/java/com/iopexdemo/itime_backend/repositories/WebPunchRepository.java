package com.iopexdemo.itime_backend.repositories;

import com.iopexdemo.itime_backend.entities.WebPunch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WebPunchRepository extends JpaRepository<WebPunch, Integer> {

//    long countByEmployeeIdAndPunchTimeBetween(
//            Integer employeeId,
//            LocalDateTime startTime,
//            LocalDateTime endTime
//    );

    List<WebPunch> findByEmployeeIdAndPunchTimeBetweenOrderByPunchTimeAsc(
            Integer employeeId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

}
