package com.appolica.weatherify.android.event;

/**
 * Created by Alexander Iliev on 04.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
public class DBForecastDeletedEvent {
    public static final int DELETED_ALL_POSITION = -1;

    private boolean currentLocation;
    private int deletedPosition;

    public DBForecastDeletedEvent(boolean currentLocation, int deletedPosition) {
        this.currentLocation = currentLocation;
        this.deletedPosition = deletedPosition;
    }

    public boolean isCurrentLocation() {
        return currentLocation;
    }

    public int getDeletedPosition() {
        return deletedPosition;
    }
}
