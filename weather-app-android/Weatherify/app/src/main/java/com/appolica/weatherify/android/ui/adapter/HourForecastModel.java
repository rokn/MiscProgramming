package com.appolica.weatherify.android.ui.adapter;

import com.appolica.weatherify.android.model.ForecastDataPoint;

public class HourForecastModel {

    private ForecastDataPoint forecastData;
    private String timeZone;

    public HourForecastModel(ForecastDataPoint forecastData, String timeZone) {
        this.forecastData = forecastData;
        this.timeZone = timeZone;
    }

    public ForecastDataPoint getForecastData() {
        return forecastData;
    }

    public void setForecastData(ForecastDataPoint forecastData) {
        this.forecastData = forecastData;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
