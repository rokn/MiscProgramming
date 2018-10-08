package com.appolica.weatherify.android.model;

import com.appolica.weatherify.android.R;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by aleksandar on 25.08.16.
 */
public enum CurrentWeatherIcon {
    @JsonProperty("clear-day")
    SUNNY(R.drawable.sunny, R.drawable.sunny_outlined),
    @JsonProperty("clear-night")
    CLEAR(R.drawable.clear, R.drawable.clear_outlined),
    @JsonProperty("rain")
    RAIN(R.drawable.rain, R.drawable.rain_outlined),
    @JsonProperty("snow")
    SNOW(R.drawable.snow, R.drawable.snow_outlined),
    @JsonProperty("sleet")
    SLEET(R.drawable.sleet, R.drawable.sleet_outlined),
    @JsonProperty("wind")
    WIND(R.drawable.windy, R.drawable.windy_outlined),
    @JsonProperty("fog")
    FOG(R.drawable.fog, R.drawable.fog_outlined),
    @JsonProperty("cloudy")
    CLOUDY(R.drawable.cloudy, R.drawable.cloudy_outlined),
    @JsonProperty("partly-cloudy-day")
    PARTLY_CLOUDLY(R.drawable.partly_cloudy, R.drawable.partly_cloudy_outlined),
    @JsonProperty("partly-cloudy-night")
    PARTLY_CLOUDLY_NIGHT(R.drawable.partly_cloudy_night, R.drawable.partly_cloudy_night_outlined);

    private int fullIconResourceId;
    private int outlineIconResourceId;

    CurrentWeatherIcon(int fullIconResourceId, int outlineIconResourceId) {
        this.fullIconResourceId = fullIconResourceId;
        this.outlineIconResourceId = outlineIconResourceId;
    }

    public int getFullIconResourceId() {
        return fullIconResourceId;
    }

    public int getOutlineIconResourceId() {
        return outlineIconResourceId;
    }
}
