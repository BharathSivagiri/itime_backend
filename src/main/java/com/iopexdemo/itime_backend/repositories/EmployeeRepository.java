package com.iopexdemo.itime_backend.repositories;

import com.iopexdemo.itime_backend.entities.EmployeeDetails;
import com.iopexdemo.itime_backend.enums.EnumEmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeDetails, Integer> {

    Optional<EmployeeDetails> findByIdAndEmpStatus(Integer id, EnumEmployeeStatus status);

    Optional<EmployeeDetails> findByEmpMailAndEmpStatus(String empMail, EnumEmployeeStatus status);

    Optional<EmployeeDetails> findByEmpMail(String empMail);

}

