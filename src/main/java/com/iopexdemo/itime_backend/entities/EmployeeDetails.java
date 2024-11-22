package com.iopexdemo.itime_backend.entities;

import com.iopexdemo.itime_backend.enums.EnumEmployeeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employee_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "emp_name", nullable = false, length = 50)
    private String empName;

    @Column(name = "emp_code", nullable = false, length = 50)
    private String empCode;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDate createdDt;

    @Enumerated(EnumType.STRING)
    @Column(name = "emp_status", length = 50)
    private EnumEmployeeStatus empStatus;

    @Column(name = "updated_dt", nullable = false)
    private LocalDate updatedDt;

    @Column(name = "updated_by", nullable = false, length = 50)
    private String updatedBy;

    @OneToMany(mappedBy = "employee")
    private List<WebPunch> webPunches;

}
