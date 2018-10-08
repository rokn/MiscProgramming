package com.appolica.weatherify.android.model;

import android.content.Context;

import com.appolica.weatherify.android.R;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by aleksandar
 */

public class LocationCoordinates implements Serializable {
    private String locationId;
    private String name;
    private double latitude;
    private double longitude;
    private boolean isCurrentLocaiton;

    public LocationCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocationString(Context context) {
        String formatString = context.getString(R.string.location_param);
        return String.format(Locale.US, formatString, latitude, longitude);
    }

    public boolean isCurrentLocaiton() {
        return isCurrentLocaiton;
    }

    public void setCurrentLocaiton(boolean currentLocaiton) {
        isCurrentLocaiton = currentLocaiton;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
