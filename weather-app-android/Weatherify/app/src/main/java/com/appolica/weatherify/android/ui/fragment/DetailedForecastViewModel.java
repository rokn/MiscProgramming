package com.appolica.weatherify.android.ui.fragment;

import android.animation.Animator;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.appolica.weatherify.android.BR;
import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.db.DBManager;
import com.appolica.weatherify.android.model.Forecast;
import com.appolica.weatherify.android.model.ForecastLocation;
import com.appolica.weatherify.android.preferences.settings.SettingsPreferences;
import com.appolica.weatherify.android.preferences.units.PreferredUnits;
import com.appolica.weatherify.android.ui.adapter.DailyForecastAdapter;
import com.appolica.weatherify.android.ui.adapter.HourlyForecastAdapter;
import com.appolica.weatherify.android.ui.view.scrollview.ScrollViewShowDetector;
import com.appolica.weatherify.android.utils.StringUtils;

import java.util.Date;

import javax.inject.Inject;

/**
 * Created by Alexander Iliev on 09.06.17.
 * Copyright © 2017 Appolica. All rights reserved.
 */
public class DetailedForecastViewModel extends BaseObservable
        implements DetailedForecastContract.ViewModel {

    private static final String TAG = "DetailedForecastViewMod";

    @Inject
    protected DBManager dbManager;
    @Inject
    protected SettingsPreferences settingsPreferences;

    private ForecastLocation forecastLocation;
    private DetailedForecastContract.Controller controller;
    private PreferredUnits preferredUnits;

    private Context context;

    private String apparentTemperature;
    private String currentMinMax;
    private String currentSummary;
    private String hourlySummary;
    private String humidityValue;
    private String precipitationValue;
    private String visibilityValue;
    private String windSpeedValue;
    private String pressureValue;
    private String cloudCoverValue;
    private String locationName;
    private String temperature;

    private TimeArcModel timeArcModel;

    private DailyForecastAdapter dailyAdapter;
    private HourlyForecastAdapter hourlyAdapter;
    private RecyclerView.LayoutManager hourlyLayoutManager;

    private int currentIconResource;

    private boolean showCurrentLocationIcon;

    private OnPropertyChangedCallback callback;

    private ForecastTime forecastTime;
    private ForecastAnimations forecastAnimations;
    private boolean precipitationAnimationRun;
    private boolean humidityAnimationRun;

    public DetailedForecastViewModel(DetailedForecastContract.Controller controller, Context context, DetailedForecastComponent injectableComponent) {
        this.context = context;
        this.controller = controller;
        injectableComponent.inject(this);
    }

    @Override
    public void onCreate(String locationId, boolean currentLocation) {
        preferredUnits = settingsPreferences.getPreferredUnits();
        forecastLocation = dbManager.getForecastLocation(locationId, currentLocation);
        hourlyAdapter = new HourlyForecastAdapter(preferredUnits);
        dailyAdapter = new DailyForecastAdapter(preferredUnits);
        hourlyLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        timeArcModel = TimeArcModel.instance(context);
        forecastAnimations = new ForecastAnimations();
        callback = new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                updateData();
            }
        };

        preferredUnits.addOnPropertyChangedCallback(callback);

        updateData();
    }

    @Override
    public void onDestroy() {
        preferredUnits.removeOnPropertyChangedCallback(callback);
        dbManager.releaseHelper();
    }

    @Override
    public void updateData() {
        forecastLocation = dbManager.getForecastLocation(forecastLocation.getGoogleLocationId(), forecastLocation.isCurrentLocation());

        if (forecastLocation == null) {
            return;
        } else if (forecastLocation.getForecast() == null) {
            setEmptyState();
            return;
        }

        Log.d(TAG, "updateData: ");
        forecastTime = ForecastTime.instance(forecastLocation.getForecast().getTimezone(), new Date().getTime());

        setApparentTemperature();
        setCurrentMinMax();
        setCurrentSummary();
        setHourlySummary();
        setHumidityValue();
        setPrecipitationValue();
        setVisibilityValue();
        setWindSpeedValue();
        setPressureValue();
        setCloudCoverValue();
        setLocationName();
        setTemperature();
        setTimeArcModel();
        setDailyAdapter();
        setHourlyAdapter();
        setCurrentIconResource();
        setShowCurrentLocationIcon();
    }

    private void setApparentTemperature() {
        apparentTemperature = StringUtils.getApparentTemperatureString(
                context,
                forecastLocation.getForecast().getCurrently().getApparentTemperature(),
                preferredUnits.getTemperatureUnit());

        notifyPropertyChanged(BR.apparentTemperature);
    }

    private void setCurrentMinMax() {
        currentMinMax = StringUtils.getCurrentMinMaxString(
                context,
                forecastLocation.getForecast().getDaily().getData().get(0).getTemperatureMax(),
                forecastLocation.getForecast().getDaily().getData().get(0).getTemperatureMin(),
                preferredUnits.getTemperatureUnit());

        notifyPropertyChanged(BR.currentMinMax);
    }

    private void setCurrentSummary() {
        currentSummary = forecastLocation.getForecast().getCurrently().getSummary();

        notifyPropertyChanged(BR.currentSummary);
    }

    private void setHourlySummary() {
        hourlySummary = forecastLocation.getForecast().getHourly().getSummary();

        notifyPropertyChanged(BR.hourlySummary);
    }

    private void setHumidityValue() {
        humidityValue = StringUtils.getHumidityValueString(
                context,
                forecastLocation.getForecast().getCurrently().getHumidity()
        );

        notifyPropertyChanged(BR.humidityValue);
    }

    private void setPrecipitationValue() {
        precipitationValue = StringUtils.getPrecipitationValueString(
                context,
                forecastLocation.getForecast().getCurrently().getPrecipProbability()
        );

        notifyPropertyChanged(BR.precipitationValue);
    }

    private void setVisibilityValue() {
        visibilityValue = StringUtils.getVisibilityValueString(
                context,
                forecastLocation.getForecast().getCurrently().getVisibility()
        );

        notifyPropertyChanged(BR.visibilityValue);
    }

    private void setWindSpeedValue() {
        windSpeedValue = StringUtils.getWindSpeedValueString(
                context,
                forecastLocation.getForecast().getCurrently().getWindSpeed(),
                forecastLocation.getForecast().getCurrently().getWindBearing(),
                preferredUnits.getSpeedUnit()
        );

        notifyPropertyChanged(BR.windSpeedValue);
    }

    private void setPressureValue() {
        pressureValue = StringUtils.getPressureValueString(
                context,
                forecastLocation.getForecast().getCurrently().getPressure()
        );

        notifyPropertyChanged(BR.pressureValue);
    }

    private void setCloudCoverValue() {
        cloudCoverValue = StringUtils.getCloudCoverValueString(
                context,
                forecastLocation.getForecast().getCurrently().getCloudCover()
        );

        notifyPropertyChanged(BR.cloudCoverValue);
    }

    private void setTimeArcModel() {
        timeArcModel.update(forecastTime, context, forecastLocation.getForecast());

        notifyPropertyChanged(BR.timeArcModel);
    }

    private void setDailyAdapter() {
        dailyAdapter.insertDailyForecastData(forecastLocation.getForecast().getDaily());

        notifyPropertyChanged(BR.dailyAdapter);
    }

    private void setHourlyAdapter() {
        hourlyAdapter.insertHourlyForecastData(forecastLocation.getForecast().getTimezone(),
                forecastLocation.getForecast().getHourly());

        notifyPropertyChanged(BR.hourlyAdapter);
    }

    private void setCurrentIconResource() {
        currentIconResource = forecastLocation.getForecast()
                .getCurrently().getIcon().getFullIconResourceId();

        notifyPropertyChanged(BR.currentIconResource);
    }

    private void setShowCurrentLocationIcon() {
        this.showCurrentLocationIcon = forecastLocation.isCurrentLocation();

        notifyPropertyChanged(BR.showCurrentLocationIcon);
    }

    private void setLocationName() {
        this.locationName = forecastLocation.getName();

        notifyPropertyChanged(BR.locationName);
    }

    private void setTemperature() {
        temperature = StringUtils.getCurrentTemperatureString(
                context,
                forecastLocation.getForecast(),
                preferredUnits.getTemperatureUnit());

        notifyPropertyChanged(BR.temperature);
    }

    private void setEmptyState() {
        String emptyState = context.getString(R.string.empty_state);

        apparentTemperature = emptyState;
        currentMinMax = emptyState;
        currentSummary = emptyState;
        hourlySummary = emptyState;
        humidityValue = emptyState;
        precipitationValue = emptyState;
        visibilityValue = emptyState;
        windSpeedValue = emptyState;
        pressureValue = emptyState;
        cloudCoverValue = emptyState;
        currentIconResource = R.drawable.clear;
        temperature = emptyState;
    }

    @Bindable
    @Override
    public String getLocationName() {
        return locationName;
    }

    @Bindable
    @Override
    public boolean getShowCurrentLocationIcon() {
        return showCurrentLocationIcon;
    }

    @Bindable
    @Override
    public int getCurrentIconResource() {
        return currentIconResource;
    }

    @Bindable
    @Override
    public String getCurrentSummary() {
        return currentSummary;
    }

    @Bindable
    @Override
    public String getTemperature() {
        return temperature;
    }

    @Bindable
    @Override
    public String getApparentTemperature() {
        return apparentTemperature;
    }

    @Bindable
    @Override
    public String getCurrentMinMax() {
        return currentMinMax;
    }

    @Override
    public void onFavoritesClick() {
        controller.onShouldOpenFavourites();
    }

    @Bindable
    @Override
    public RecyclerView.Adapter getHourlyAdapter() {
        return hourlyAdapter;
    }

    @Bindable
    @Override
    public RecyclerView.LayoutManager getHourlyLayoutManager() {
        return hourlyLayoutManager;
    }

    @Bindable
    @Override
    public BaseAdapter getDailyAdapter() {
        return dailyAdapter;
    }

    @Bindable
    @Override
    public String getHumidityValue() {
        return humidityValue;
    }

    @Bindable
    @Override
    public String getPrecipitationValue() {
        return precipitationValue;
    }

    @Bindable
    @Override
    public String getVisibilityValue() {
        return visibilityValue;
    }

    @Bindable
    @Override
    public String getWindSpeedValue() {
        return windSpeedValue;
    }

    @Bindable
    @Override
    public String getPressureValue() {
        return pressureValue;
    }

    @Bindable
    @Override
    public String getCloudCoverValue() {
        return cloudCoverValue;
    }

    @Bindable
    @Override
    public TimeArcModel getTimeArcModel() {
        return timeArcModel;
    }

    @Bindable
    @Override
    public String getHourlySummary() {
        return hourlySummary;
    }

    public void precipitationScrolledIn(View view) {
        if (forecastLocation == null || forecastLocation.getForecast() == null) {
            return;
        }

        Forecast forecast = forecastLocation.getForecast();
        if (!precipitationAnimationRun) {
            precipitationAnimationRun = true;

            final double precipLevel = forecast.getCurrently().getPrecipProbability();

            final ImageView imageView = (ImageView) view;
            final Animator animation =
                    forecastAnimations.createPrecipitationAnimation(imageView, (float) precipLevel);

            animation.start();
        }
    }

    public void humidityScrolledIn(View view) {
        if (forecastLocation == null || forecastLocation.getForecast() == null) {
            return;
        }

        Forecast forecast = forecastLocation.getForecast();
        if (!humidityAnimationRun) {
            humidityAnimationRun = true;

            final float humidityLevel = (float) forecast.getCurrently().getHumidity();

            final Animator animation =
                    forecastAnimations.createHumidityAnimation((ImageView) view, humidityLevel);

            animation.start();
        }
    }

    public void arcScrolledIn(ScrollViewShowDetector.ObservedGroup group) {
        timeArcModel.arcScrolledIn(group);
    }
}
