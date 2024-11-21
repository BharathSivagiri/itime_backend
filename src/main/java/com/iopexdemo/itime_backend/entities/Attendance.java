package com.iopexdemo.itime_backend.entities;

import com.iopexdemo.itime_backend.enums.DBRecordStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "employee_id")
    private int employeeId;

    @Column(name = "punch_in_time")
    private LocalDateTime punchInTime;

    @Column(name = "punch_out_time")
    private LocalDateTime punchOutTime;

    @Column(name = "total_hours")
    private Double totalHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_status")
    private DBRecordStatus recStatus;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private String createdDate;

    @Column(name = "last_updated_by")
    private String updatedBy;

    @Column(name = "last_updated_date")
    private String updatedDate;

    public Attendance(int id, int employeeId, LocalDateTime punchInTime, LocalDateTime punchOutTime, Double totalHours, DBRecordStatus recStatus, String createdBy, String createdDate, String updatedBy, String updatedDate) {
        this.id = id;
        this.employeeId = employeeId;
        this.punchInTime = punchInTime;
        this.punchOutTime = punchOutTime;
        this.totalHours = totalHours;
        this.recStatus = recStatus;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.updatedBy = updatedBy;
        this.updatedDate = updatedDate;
    }

    public Attendance() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getPunchInTime() {
        return punchInTime;
    }

    public void setPunchInTime(LocalDateTime punchInTime) {
        this.punchInTime = punchInTime;
    }

    public LocalDateTime getPunchOutTime() {
        return punchOutTime;
    }

    public void setPunchOutTime(LocalDateTime punchOutTime) {
        this.punchOutTime = punchOutTime;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    public DBRecordStatus getRecStatus() {
        return recStatus;
    }

    public void setRecStatus(DBRecordStatus recStatus) {
        this.recStatus = recStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }
}
