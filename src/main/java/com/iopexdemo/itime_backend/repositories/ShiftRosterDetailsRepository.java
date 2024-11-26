package com.iopexdemo.itime_backend.repositories;

import com.iopexdemo.itime_backend.entities.ShiftRosterDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ShiftRosterDetailsRepository extends JpaRepository<ShiftRosterDetails, Integer> {
    ShiftRosterDetails findByEmployeeIdAndShiftDate(Integer employeeId, LocalDate shiftDate);
}
