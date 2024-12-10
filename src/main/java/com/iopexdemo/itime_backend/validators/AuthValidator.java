package com.iopexdemo.itime_backend.validators;

import com.iopexdemo.itime_backend.entities.EmployeeDetails;
import com.iopexdemo.itime_backend.exceptions.custom.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthValidator {

    @Autowired
    PasswordEncoder passwordEncoder;

    public void validateCredentials(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new AuthException("Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthException("Password cannot be empty");
        }
    }

    public void validateEmployee(EmployeeDetails employee, String password) {
        if (employee == null) {
            throw new AuthException("E-Mail ID cannot be empty");
        }

        if (!passwordEncoder.matches(password, employee.getEmpPassword())) {
            throw new AuthException("Invalid password");
        }
    }
}

