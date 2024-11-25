package com.iopexdemo.itime_backend.repositories;

import com.iopexdemo.itime_backend.entities.ShiftRosterDetails;
import com.iopexdemo.itime_backend.enums.EnumRecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ShiftRosterRepository extends JpaRepository<ShiftRosterDetails, Integer> {

    Optional<ShiftRosterDetails> findByEmployeeIdAndShiftDateAndRecordStatus(
        Integer employeeId,
        LocalDate shiftDate,
        EnumRecordStatus recordStatus
    );

}