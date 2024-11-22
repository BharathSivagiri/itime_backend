package com.iopexdemo.itime_backend.entities;

import com.iopexdemo.itime_backend.enums.EnumPunchType;
import com.iopexdemo.itime_backend.enums.EnumStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "web_punch")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebPunch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private EmployeeDetails employee;

    @Column(name = "punch_time", nullable = false)
    private LocalDateTime punchTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "punch_type", length = 50)
    private EnumPunchType punchType;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDate createdDt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private EnumStatus status;

    @Column(name = "updated_dt", nullable = false)
    private LocalDate updatedDt;

    @Column(name = "updated_by", nullable = false, length = 50)
    private String updatedBy;


}
