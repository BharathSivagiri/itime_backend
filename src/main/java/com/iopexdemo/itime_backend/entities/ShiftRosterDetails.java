package com.iopexdemo.itime_backend.entities;

import com.iopexdemo.itime_backend.enums.EnumRecordStatus;
import com.iopexdemo.itime_backend.enums.EnumShiftStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "shift_roster_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftRosterDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "emp_id")
    private Integer employeeId;

    @Column(name = "shift_id")
    private Integer shiftId;

    @Column(name = "shift_date")
    private LocalDate shiftDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift_status")
    private EnumShiftStatus shiftStatus;

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

    @ManyToOne
    @JoinColumn(name = "shift_id", insertable = false, updatable = false)
    private ShiftDetails shiftDetails;
}
