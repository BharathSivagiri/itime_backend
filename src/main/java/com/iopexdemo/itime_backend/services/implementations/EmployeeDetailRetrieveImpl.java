package com.iopexdemo.itime_backend.services.implementations;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.iopexdemo.itime_backend.entities.EmployeeDetails;
import com.iopexdemo.itime_backend.repositories.EmployeeRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.ArrayList;

@Service
public class EmployeeDetailRetrieveImpl implements UserDetailsService {

    @Autowired
    EmployeeRepository employeeDetailsRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        EmployeeDetails employee = employeeDetailsRepository.findByEmpMail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new User(employee.getEmpMail(), employee.getEmpPassword(), new ArrayList<>());
    }
}

