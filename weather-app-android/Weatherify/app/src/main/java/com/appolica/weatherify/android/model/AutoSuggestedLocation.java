package com.appolica.weatherify.android.model;

/**
 * Created by aleksandar
 */

public class AutoSuggestedLocation {
    private String name;
    private String placeID;

    public AutoSuggestedLocation(String location, String placeID) {
        this.name = location;
        this.placeID = placeID;
    }

    public String getName() {
        return name;
    }

    public String getPlaceID() {
        return placeID;
    }
}
