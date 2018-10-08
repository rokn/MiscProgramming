package com.appolica.weatherify.android.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.TextAppearanceSpan;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;
import com.appolica.weatherify.android.model.Forecast;
import com.appolica.weatherify.android.model.ForecastDataPoint;
import com.appolica.weatherify.android.preferences.units.SpeedUnit;
import com.appolica.weatherify.android.preferences.units.TemperatureUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by aleksandar on 25.08.16.
 */
@Scopes.Application
public class StringUtils {
    private static final int WIND_BEARING_NORTH_EAST_SMALL_LIMIT = 23;
    private static final int WIND_BEARING_NORTH_EAST_BIG_LIMIT = 67;
    private static final int WIND_BEARING_EAST_SMALL_LIMIT = 68;
    private static final int WIND_BEARING_EAST_BIG_LIMIT = 112;
    private static final int WIND_BEARING_SOUTH_EAST_SMALL_LIMIT = 113;
    private static final int WIND_BEARING_SOUTH_EAST_BIG_LIMIT = 157;
    private static final int WIND_BEARING_SOUTH_SMALL_LIMIT = 158;
    private static final int WIND_BEARING_SOUTH_BIG_LIMIT = 202;
    private static final int WIND_BEARING_SOUTH_WEST_SMALL_LIMIT = 203;
    private static final int WIND_BEARING_SOUTH_WEST_BIG_LIMIT = 247;
    private static final int WIND_BEARING_WEST_SMALL_LIMIT = 248;
    private static final int WIND_BEARING_WEST_BIG_LIMIT = 292;
    private static final int WIND_BEARING_NORTH_WEST_SMALL_LIMIT = 293;
    private static final int WIND_BEARING_NORTH_WEST_BIG_LIMIT = 337;
    private static final int MIDDAY = 12;
    private static final int LAST_HOUR_OF_THE_DAY = 0;
    public static final String DAY_FORMAT = "EEEE";
    public static final String HOUR_FORMAT_24 = "HH:00";
    public static final String HOUR_FORMAT_12 = "hh aa";
    public static final String HOUR_FORMAT_FAVOURITES_12 = "hh:mma";
    public static final String HOUR_FORMAT_FAVOURITES_24 = "HH:mm";

    @Nullable
    public static String getApparentTemperatureString(
            Context context,
            final double apparentTemperature,
            TemperatureUnit temperatureUnit) {

        if (temperatureUnit == null) {
            return null;
        }

        final double temperature =
                UnitsUtil.celsiusTo(apparentTemperature, temperatureUnit);

        return context.getString(R.string.apparent_temperature, Math.round(temperature));
    }

    @Nullable
    public static String getCurrentTemperatureString(
            Context context,
            final Forecast forecast,
            TemperatureUnit temperatureUnit) {

        if (forecast == null) {
            return context.getString(R.string.empty_state);
        }

        final double convertedTemperature =
                UnitsUtil.celsiusTo(forecast.getCurrently().getTemperature(), temperatureUnit);

        return context.getString(R.string.current_temperature, Math.round(convertedTemperature));
    }

    @Nullable
    public static String getCurrentMinMaxString(
            Context context,
            final double maxTemperature,
            final double minTemperature,
            TemperatureUnit temperatureUnit) {

        if (temperatureUnit == null) {
            return null;
        }

        final double convertedMin = UnitsUtil.celsiusTo(minTemperature, temperatureUnit);
        final double convertedMax = UnitsUtil.celsiusTo(maxTemperature, temperatureUnit);

        return context.getString(R.string.max_min_temperature,
                Math.round(convertedMax),
                Math.round(convertedMin));
    }

    public static String getHumidityValueString(Context context, final double humidity) {
        return context.getString(R.string.humidity, Math.round(humidity * 100));
    }

    public static String getPrecipitationValueString(Context context, final double precipProbability) {
        return context.getString(R.string.precipitation, Math.round(precipProbability * 100));
    }

    public static String getVisibilityValueString(Context context, final double visibility) {
        return context.getString(R.string.visibility, visibility);
    }

    @Nullable
    public static String getWindSpeedValueString(
            Context context,
            final double windSpeed,
            final int windBearing,
            SpeedUnit speedUnit) {

        if (speedUnit == null) {
            return null;
        }

        final double convertedWindSpeed =
                UnitsUtil.metersPerSecondTo(windSpeed, speedUnit);

        String windDirectionString = "";
        String units = context.getString(speedUnit.getUnitResourceId());

        if (windSpeed > 0) {
            windDirectionString = context.getString(getWindDirection(windBearing));
        }

        return context.getString(
                R.string.wind,
                windDirectionString,
                convertedWindSpeed,
                units);
    }

    private static int getWindDirection(final int windBearing) {
        WindDirections windDirection;

        if (windBearing >= WIND_BEARING_NORTH_EAST_SMALL_LIMIT
                && windBearing < WIND_BEARING_NORTH_EAST_BIG_LIMIT) {

            windDirection = WindDirections.WIND_NE;

        } else if (windBearing >= WIND_BEARING_EAST_SMALL_LIMIT
                && windBearing < WIND_BEARING_EAST_BIG_LIMIT) {

            windDirection = WindDirections.WIND_E;

        } else if (windBearing >= WIND_BEARING_SOUTH_EAST_SMALL_LIMIT
                && windBearing < WIND_BEARING_SOUTH_EAST_BIG_LIMIT) {

            windDirection = WindDirections.WIND_SE;

        } else if (windBearing >= WIND_BEARING_SOUTH_SMALL_LIMIT
                && windBearing < WIND_BEARING_SOUTH_BIG_LIMIT) {

            windDirection = WindDirections.WIND_S;

        } else if (windBearing >= WIND_BEARING_SOUTH_WEST_SMALL_LIMIT
                && windBearing < WIND_BEARING_SOUTH_WEST_BIG_LIMIT) {

            windDirection = WindDirections.WIND_SW;

        } else if (windBearing >= WIND_BEARING_WEST_SMALL_LIMIT
                && windBearing < WIND_BEARING_WEST_BIG_LIMIT) {

            windDirection = WindDirections.WIND_W;

        } else if (windBearing >= WIND_BEARING_NORTH_WEST_SMALL_LIMIT
                && windBearing < WIND_BEARING_NORTH_WEST_BIG_LIMIT) {

            windDirection = WindDirections.WIND_NW;

        } else {

            windDirection = WindDirections.WIND_N;

        }

        return windDirection.getStringResourceId();
    }

    public static String getPressureValueString(Context context, final double pressure) {
        return context.getString(R.string.pressure, pressure);
    }

    public static String getCloudCoverValueString(Context context, final double cloudCover) {
        return context.getString(R.string.cloud_cover, (Math.round(cloudCover * 100)));
    }

    public static String getDetailsTimearcTimeString(final long time) {
        return DateFormat.format("HH:mm", time).toString();
    }

    public static String getHourlyTimeString(Context context, final long time, String timeZone) {
        final DateTimeZone dateTimeZone = DateTimeZone.forID(timeZone);
        final DateTime dateTime = new DateTime(time, dateTimeZone);

        final DateTimeFormatter formatter;
        if (DateFormat.is24HourFormat(context)) {
            formatter = DateTimeFormat.forPattern(HOUR_FORMAT_24);
        } else {
            formatter = DateTimeFormat.forPattern(HOUR_FORMAT_12);
        }

        return dateTime.toString(formatter);
    }

    @Nullable
    public static String getHourlyTemperatureString(
            Context context,
            final double temperature,
            TemperatureUnit temperatureUnit) {

        final double convertedTemperature =
                UnitsUtil.celsiusTo(temperature, temperatureUnit);

        return context.getString(R.string.hourly_temperature, Math.round(convertedTemperature));
    }

    public static String getDailyDateString(final long date) {
        return new SimpleDateFormat(DAY_FORMAT, Locale.getDefault()).format(date);
    }

    @Nullable
    public static String getDailyPrecipitaionValue(Context context, final double precipProbability) {

        if (precipProbability < 0.2) {
            return null;
        }

        return context.getString(R.string.daily_precipitation, Math.round(precipProbability * 100));
    }

    @Nullable
    public static String getMinMaxStringPlain(
            Context context,
            final double maxTemperature,
            final double minTemperature,
            TemperatureUnit temperatureUnit) {

        if (temperatureUnit == null) {
            return null;
        }

        final double convertedMin = UnitsUtil.celsiusTo(minTemperature, temperatureUnit);
        final double convertedMax = UnitsUtil.celsiusTo(maxTemperature, temperatureUnit);

        return context.getString(R.string.daily_min_max, Math.round(convertedMax), Math.round(convertedMin));
    }

    public static String getMinMaxStringPlain(
            Context context,
            ForecastDataPoint forecastDataPoint,
            TemperatureUnit temperatureUnit) {

        if(forecastDataPoint == null)
            return context.getString(R.string.empty_state);

        return getMinMaxStringPlain(
                context,
                forecastDataPoint.getTemperatureMax(),
                forecastDataPoint.getTemperatureMin(),
                temperatureUnit);
    }

    @Nullable
    public static SpannableString getDailyMinMaxString(
            Context context,
            ForecastDataPoint forecastDataPoint,
            TemperatureUnit temperatureUnit) {

        if (temperatureUnit == null) {
            return null;
        }

        String temperatureString = getMinMaxStringPlain(
                context,
                forecastDataPoint.getTemperatureMax(),
                forecastDataPoint.getTemperatureMin(),
                temperatureUnit);
        String temperatureSeperator = context.getString(R.string.temperatureSeperator);
        int seperatorIndex = temperatureString.indexOf(temperatureSeperator) + temperatureSeperator.length();
        int length = temperatureString.length();

        SpannableString styledString = new SpannableString(temperatureString);
        TextAppearanceSpan mainStyle = new TextAppearanceSpan(context, R.style.mainScreenText);
        TextAppearanceSpan lightStyle = new TextAppearanceSpan(context, R.style.mainScreenText_Light);


        styledString.setSpan(mainStyle, 0, seperatorIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        styledString.setSpan(lightStyle, seperatorIndex, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return styledString;
    }

    public static String getFavouritesMinMaxString(
            Context context,
            ForecastDataPoint forecastDataPoint,
            TemperatureUnit temperatureUnit) {

        if (forecastDataPoint == null) {
            return context.getString(R.string.empty_state);
        }

        return getMinMaxStringPlain(
                context,
                forecastDataPoint.getTemperatureMax(),
                forecastDataPoint.getTemperatureMin(),
                temperatureUnit);
    }

    public static String getWeekDay(long time) {
        return new SimpleDateFormat(DAY_FORMAT, Locale.getDefault()).format(time);
    }

    public static String getFavouritesTimeString(Context context, long time, String timeZone) {
        final DateTimeZone dateTimeZone = DateTimeZone.forID(timeZone);
        final DateTime dateTime = new DateTime(time, dateTimeZone);

        final DateTimeFormatter formatter;
        if (DateFormat.is24HourFormat(context)) {
            formatter = DateTimeFormat.forPattern(HOUR_FORMAT_FAVOURITES_24);
        } else {
            formatter = DateTimeFormat.forPattern(HOUR_FORMAT_FAVOURITES_12);
        }

        return dateTime.toString(formatter);
    }
}
