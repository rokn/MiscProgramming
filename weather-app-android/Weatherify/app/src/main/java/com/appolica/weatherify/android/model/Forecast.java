package com.appolica.weatherify.android.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by aleksandar on 24.08.16.
 */
@DatabaseTable(tableName = "forecast")
public class Forecast {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String timezone;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, unique = true)
    private ForecastDataPoint currently;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, unique = true)
    private ForecastDataBlock hourly;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, unique = true)
    private ForecastDataBlock daily;

    public Forecast() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ForecastDataPoint getCurrently() {
        return currently;
    }

    public void setCurrently(ForecastDataPoint currently) {
        this.currently = currently;
    }

    public ForecastDataBlock getHourly() {
        return hourly;
    }

    public void setHourly(ForecastDataBlock hourly) {
        this.hourly = hourly;
    }

    public ForecastDataBlock getDaily() {
        return daily;
    }

    public void setDaily(ForecastDataBlock daily) {
        this.daily = daily;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
