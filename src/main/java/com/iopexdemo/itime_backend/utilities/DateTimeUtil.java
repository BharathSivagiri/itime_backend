package com.iopexdemo.itime_backend.utilities;

import java.time.Duration;

public final class DateTimeUtil {

    private DateTimeUtil() {

    }

    public static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}


