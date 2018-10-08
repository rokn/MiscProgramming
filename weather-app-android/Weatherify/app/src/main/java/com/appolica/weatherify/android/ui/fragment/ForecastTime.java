package com.appolica.weatherify.android.ui.fragment;

import android.content.Context;

import com.appolica.weatherify.android.model.ForecastDataPoint;
import com.appolica.weatherify.android.utils.TimeUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

public class ForecastTime {

    private TimeContext timeContext;

    public static ForecastTime instance(String timeZone, long timeMillis) {
        return new ForecastTime(TimeContext.forMillis(timeZone, timeMillis));
    }

    public ForecastTime(TimeContext timeContext) {
        this.timeContext = timeContext;
    }

    public int getPercentageOfDayNight(List<ForecastDataPoint> dailyForecast) {
        final ForecastDataPoint todayForecast = dailyForecast.get(0);
        final ForecastDataPoint tomorrowForecast = dailyForecast.get(1);

        final long startMillis;
        final long endMillis;
        if (isBetween(todayForecast.getSunriseTime(), todayForecast.getSunsetTime())) {
            startMillis = todayForecast.getSunriseTime();
            endMillis = todayForecast.getSunsetTime();
        } else {

            DateTime sunsetTime;
            if (timeContext.time.isBefore(todayForecast.getSunriseTime())) {
                sunsetTime = new DateTime(todayForecast.getSunsetTime(), timeContext.getTimeZone());
                sunsetTime = sunsetTime.minusDays(1);

                endMillis = todayForecast.getSunriseTime();
            } else {
                sunsetTime = new DateTime(todayForecast.getSunsetTime(), timeContext.getTimeZone());
                endMillis = tomorrowForecast.getSunriseTime();
            }

            startMillis = sunsetTime.getMillis();

        }

        return TimeUtils.getPercentageOfTimeDuration(timeContext.time.getMillis(), startMillis, endMillis);
    }

    public boolean isBetween(long startMillis, long endMillis) {
        return TimeUtils.isBetween(timeContext.time, startMillis, endMillis);
    }

    public void setTimeZone(String timeZone) {
        final DateTimeZone dateTimeZone = DateTimeZone.forID(timeZone);
        setTimeZone(dateTimeZone);
    }

    public void setTimeZone(DateTimeZone timeZone) {
        timeContext.setTimeZone(timeZone);
    }

    public String getTimeZone() {
        return timeContext.getTimeZone().getID();
    }

    public String getTimeString(Context context) {
        return TimeUtils.getTimeString(
                context,
                timeContext.getTime().getMillis(),
                timeContext.getTimeZone().getID());
    }

    public static class TimeContext {
        private DateTime time;

        public static TimeContext forMillis(String timeZone, long timeMillis) {
            final DateTimeZone dateTimeZone = DateTimeZone.forID(timeZone);
            final DateTime dateTime = new DateTime(timeMillis, dateTimeZone);

            return new TimeContext(dateTime);
        }

        public TimeContext(DateTime time) {
            this.time = time;
        }

        public DateTimeZone getTimeZone() {
            return time.getZone();
        }

        public void setTimeZone(DateTimeZone timeZone) {
            this.time = this.time.withZone(timeZone);
        }

        public DateTime getTime() {
            return time;
        }

        public void setTime(DateTime time) {
            this.time = time;
        }
    }
}
