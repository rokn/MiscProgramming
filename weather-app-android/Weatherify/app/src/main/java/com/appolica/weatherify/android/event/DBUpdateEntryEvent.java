package com.appolica.weatherify.android.event;

/**
 * Created by Alexander Iliev
 */

public class DBUpdateEntryEvent {
    private String forecastLocationId;
    private boolean isCurrentLocation;

    public DBUpdateEntryEvent(String forecastLocationId, boolean isCurrentLocation) {
        this.forecastLocationId = forecastLocationId;
        this.isCurrentLocation = isCurrentLocation;
    }

    public String getForecastLocationId() {
        return forecastLocationId;
    }

    public boolean isCurrentLocation() {
        return isCurrentLocation;
    }
}
