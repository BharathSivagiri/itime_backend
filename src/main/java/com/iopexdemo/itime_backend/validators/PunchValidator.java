package com.iopexdemo.itime_backend.validators;

import com.iopexdemo.itime_backend.dto.PunchRequest;
import com.iopexdemo.itime_backend.entities.EmployeeDetails;
import com.iopexdemo.itime_backend.enums.EnumEmployeeStatus;
import com.iopexdemo.itime_backend.exceptions.custom.CustomException;
import com.iopexdemo.itime_backend.repositories.EmployeeRepository;
import com.iopexdemo.itime_backend.utilities.constants.AppMessages;
import org.springframework.stereotype.Component;

@Component
public class PunchValidator {
    public void validatePunchRequest(PunchRequest request, EmployeeDetails employee) {
        if (employee == null) {
            throw new CustomException(AppMessages.EMPLOYEE_NOT_FOUND);
        }
        if (!EnumEmployeeStatus.ACTIVE.equals(employee.getEmpStatus())) {
            throw new CustomException(AppMessages.EMPLOYEE_RECORD_NOT_FOUND);
        }
    }

    public EmployeeDetails getValidatedEmployee(Integer employeeId, EmployeeRepository employeeRepository) {
        return employeeRepository.findByIdAndEmpStatus(employeeId, EnumEmployeeStatus.ACTIVE)
                .orElseThrow(() -> new CustomException(AppMessages.EMPLOYEE_NOT_FOUND));
    }
}
