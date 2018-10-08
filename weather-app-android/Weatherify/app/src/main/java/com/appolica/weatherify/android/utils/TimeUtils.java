package com.appolica.weatherify.android.utils;

import android.content.Context;
import android.text.format.DateFormat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Locale;

public class TimeUtils {

    public static String getTimeString(final Context context, final long timeMillis, final String timeZone) {
        final DateTimeZone dateTimeZone = DateTimeZone.forID(timeZone);
        final DateTime dateTime = new DateTime(timeMillis, dateTimeZone);

        DateTimeFormatter formatter;
        if (DateFormat.is24HourFormat(context)) {
            formatter = DateTimeFormat.forPattern("HH:mm");
        } else {
            formatter = DateTimeFormat.forPattern("hh:mm aa");
        }

        return dateTime.toString(formatter);
    }

    public static int getPercentageOfTimeDuration(long timeMillis, long startMillis, long endMillis) {
        final long duration = endMillis - startMillis;
        final long current = timeMillis - startMillis;
        final int percentage = (int) ((100 * current) / duration);

        return Math.max(0, Math.min(percentage, 100));
    }

    public static boolean isBetween(DateTime time, long startMillis, long endMillis) {
        return time.isAfter(startMillis) && time.isBefore(endMillis);
    }

    public static boolean isMidnight(long time, String timeZone) {
        final DateTimeZone dateTimeZone = DateTimeZone.forID(timeZone);
        final DateTime dateTime = new DateTime(time, dateTimeZone);
        return dateTime.withTimeAtStartOfDay().equals(dateTime);
    }

    public static boolean isToday(long time, String timeZone) {
        final DateTimeZone dateTimeZone = DateTimeZone.forID(timeZone);
        final DateTime dateTime = new DateTime(time, dateTimeZone);
        final DateTime now = DateTime.now(dateTimeZone);
        return now.getDayOfWeek() == dateTime.getDayOfWeek();
    }
}
