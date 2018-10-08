package com.appolica.weatherify.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Alexander Iliev
 */

@DatabaseTable(tableName = "forecast_location")
public class ForecastLocation {
    public static final String DATE_CREATED_COLUMN_NAME = "created_on";
    public static final String COLUMN_IS_CURRENT_LOCATION = "isCurrentLocation";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_GOOGLE_ID = "googleLocationId";
    public static final String ORDER_POSITION = "orderPosition";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = COLUMN_NAME)
    private String name;
    @DatabaseField(columnName = COLUMN_IS_CURRENT_LOCATION)
    private boolean isCurrentLocation;
    @DatabaseField
    private double latitude;
    @DatabaseField
    private double longitude;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, unique = true)
    private Forecast forecast;
    @DatabaseField(columnName = COLUMN_GOOGLE_ID, unique = true, index = true)
    private String googleLocationId;
    @DatabaseField(columnName = ORDER_POSITION, canBeNull = false)
    private int orderPosition;

    public ForecastLocation() {
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCurrentLocation() {
        return isCurrentLocation;
    }

    public void setCurrentLocation(boolean currentLocation) {
        isCurrentLocation = currentLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getGoogleLocationId() {
        return googleLocationId;
    }

    public void setGoogleLocationId(String googleLocationId) {
        this.googleLocationId = googleLocationId;
    }

    public int getOrderPosition() {
        return orderPosition;
    }

    public void setOrderPosition(int orderPosition) {
        this.orderPosition = orderPosition;
    }

    public static class Builder {
        private String name;
        private boolean isCurrentLocation;
        private double latitude;
        private double longitude;
        private Forecast forecast;
        private String googleLocationId;
        private int orderPosition;

        public ForecastLocation.Builder setName(String name) {
            this.name = name;
            return this;
        }

        public ForecastLocation.Builder setCurrentLocation(boolean isCurrentLocation) {
            this.isCurrentLocation = isCurrentLocation;
            return this;
        }

        public ForecastLocation.Builder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public ForecastLocation.Builder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public ForecastLocation.Builder setForecast(Forecast forecast) {
            this.forecast = forecast;
            return this;
        }

        public ForecastLocation.Builder setGoogleLocationId(String googleLocationId) {
            this.googleLocationId = googleLocationId;
            return this;
        }

        public ForecastLocation.Builder setOrderPosition(int orderPosition) {
            this.orderPosition = orderPosition;
            return this;
        }

        public ForecastLocation build() {
            ForecastLocation forecastLocation = new ForecastLocation();

            forecastLocation.setName(name);
            forecastLocation.setCurrentLocation(isCurrentLocation);
            forecastLocation.setLatitude(latitude);
            forecastLocation.setLongitude(longitude);
            forecastLocation.setForecast(forecast);
            forecastLocation.setGoogleLocationId(googleLocationId);
            forecastLocation.setOrderPosition(orderPosition);

            return forecastLocation;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ForecastLocation) {
            return this.id == ((ForecastLocation) obj).getId();
        }

        return super.equals(obj);
    }
}
