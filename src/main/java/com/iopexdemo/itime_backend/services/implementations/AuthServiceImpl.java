package com.iopexdemo.itime_backend.services.implementations;

import com.iopexdemo.itime_backend.entities.EmployeeDetails;
import com.iopexdemo.itime_backend.enums.EnumEmployeeStatus;
import com.iopexdemo.itime_backend.repositories.EmployeeRepository;
import com.iopexdemo.itime_backend.services.AuthService;
import com.iopexdemo.itime_backend.utilities.JwtUtil;
import com.iopexdemo.itime_backend.validators.AuthValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AuthValidator authValidator;

    @Override
    public String login(String email, String password) {
        logger.info("Attempting login for user with email : {}", email);

        // Validate input credentials
        authValidator.validateCredentials(email, password);

        // Get employee details
        EmployeeDetails employee = employeeRepository
                .findByEmpMailAndEmpStatus(email, EnumEmployeeStatus.ACTIVE)
                .orElse(null);

        // Validate employee and password
        authValidator.validateEmployee(employee, password);

        logger.info("Login for user with email : {} is successful", email);
        return jwtUtil.generateToken(email, employee.getId());
    }
}
