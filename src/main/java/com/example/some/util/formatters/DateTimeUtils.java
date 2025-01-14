package com.example.some.util.formatters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateTimeUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeUtils() {}

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }

    public static String getTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);

        if (minutes < 60) return minutes + " minutes ago";
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        if (hours < 24) return hours + " hours ago";
        long days = ChronoUnit.DAYS.between(dateTime, now);
        if (days < 30) return days + " days ago";
        long months = ChronoUnit.MONTHS.between(dateTime.toLocalDate(), now.toLocalDate());
        if (months < 12) return months + " months ago";
        return ChronoUnit.YEARS.between(dateTime.toLocalDate(), now.toLocalDate()) + " years ago";
    }
}