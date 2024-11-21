package com.iopexdemo.itime_backend.dto;

import java.time.LocalDateTime;

public class AttendanceDTO {
    private int employeeId;
    private LocalDateTime punchInTime;
    private LocalDateTime punchOutTime;
    private Double totalHours;

    public AttendanceDTO(int employeeId, LocalDateTime punchInTime, LocalDateTime punchOutTime, Double totalHours) {
        this.employeeId = employeeId;
        this.punchInTime = punchInTime;
        this.punchOutTime = punchOutTime;
        this.totalHours = totalHours;
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
}
