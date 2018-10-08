package com.appolica.weatherify.android.event;

/**
 * Created by aleksandar
 */

public class DBForecastAddedEvent {
    private boolean currentLocation;
    private String locationId;

    public DBForecastAddedEvent(boolean currentLocation, String locationId) {
        this.currentLocation = currentLocation;
        this.locationId = locationId;
    }

    public boolean isCurrentLocation() {
        return currentLocation;
    }

    public String getLocationId() {
        return locationId;
    }
}
