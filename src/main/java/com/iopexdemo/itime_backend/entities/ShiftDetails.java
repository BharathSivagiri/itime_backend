package com.iopexdemo.itime_backend.entities;

import com.iopexdemo.itime_backend.enums.EnumRecordStatus;
import com.iopexdemo.itime_backend.enums.EnumShiftType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "shift_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "shift_name")
    private String shiftName;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type")
    private EnumShiftType shiftType;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "created_dt")
    private LocalDate createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_status")
    private EnumRecordStatus recordStatus;

    @Column(name = "updated_dt")
    private LocalDate updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;
}
