package com.iopexdemo.itime_backend.enums;

public enum EnumShiftStatus {
    SCHEDULED("SCHEDULED"),
    CANCELLED("CANCELLED");

    public final String enumShiftStatus;

    EnumShiftStatus(String enumShiftStatus) {
        this.enumShiftStatus = enumShiftStatus;
    }
}
