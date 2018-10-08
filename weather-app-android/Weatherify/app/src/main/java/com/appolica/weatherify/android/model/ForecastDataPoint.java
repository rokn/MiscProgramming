package com.appolica.weatherify.android.model;

import com.appolica.weatherify.android.json.TimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by aleksandar
 */
@DatabaseTable(tableName = "forecast_data_point")
public class ForecastDataPoint {
    @DatabaseField(generatedId = true)
    private int id;
    @JsonDeserialize(using = TimeDeserializer.class)
    @DatabaseField
    private long time;
    @DatabaseField
    private String summary;
    @DatabaseField
    private CurrentWeatherIcon icon;
    @DatabaseField
    @JsonDeserialize(using = TimeDeserializer.class)
    private long sunriseTime;
    @DatabaseField
    @JsonDeserialize(using = TimeDeserializer.class)
    private long sunsetTime;
    @DatabaseField
    private double precipProbability;
    @DatabaseField
    private double temperature;
    @DatabaseField
    private double apparentTemperature;
    @DatabaseField
    private double temperatureMin;
    @DatabaseField
    private double temperatureMax;
    @DatabaseField
    private double humidity;
    @DatabaseField
    private double windSpeed;
    @DatabaseField
    private int windBearing;
    @DatabaseField
    private double visibility;
    @DatabaseField
    private double cloudCover;
    @DatabaseField
    private double pressure;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private ForecastDataBlock forecastDataBlock;

    public ForecastDataPoint() {
    }

    public ForecastDataBlock getForecastDataBlock() {
        return forecastDataBlock;
    }

    public void setForecastDataBlock(ForecastDataBlock forecastDataBlock) {
        this.forecastDataBlock = forecastDataBlock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public CurrentWeatherIcon getIcon() {
        return icon;
    }

    public void setIcon(CurrentWeatherIcon icon) {
        this.icon = icon;
    }

    public long getSunriseTime() {
        return sunriseTime;
    }

    public void setSunriseTime(long sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public long getSunsetTime() {
        return sunsetTime;
    }

    public void setSunsetTime(long sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    public double getPrecipProbability() {
        return precipProbability;
    }

    public void setPrecipProbability(double precipProbability) {
        this.precipProbability = precipProbability;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getApparentTemperature() {
        return apparentTemperature;
    }

    public void setApparentTemperature(double apparentTemperature) {
        this.apparentTemperature = apparentTemperature;
    }

    public double getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public double getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getWindBearing() {
        return windBearing;
    }

    public void setWindBearing(int windBearing) {
        this.windBearing = windBearing;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(double cloudCover) {
        this.cloudCover = cloudCover;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }
}
