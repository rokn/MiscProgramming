package com.appolica.weatherify.android;

import android.content.Intent;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.appolica.weatherify.android.dependencyinjection.graph.app.AppComponent;
import com.appolica.weatherify.android.dependencyinjection.graph.app.AppModule;
import com.appolica.weatherify.android.dependencyinjection.graph.app.DaggerAppComponent;
import com.appolica.weatherify.android.service.GCMService;
import com.appolica.weatherify.android.service.LocationManagerService;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import net.danlew.android.joda.JodaTimeAndroid;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Alexander Iliev on 25.08.16.
 * Copyright © 2017 Appolica. All rights reserved.
 */
public class WeatherifyApplication extends MultiDexApplication {
    //todo set accurate interval when done testing
    private static final int FETCH_FORECAST_INTERVAL_IN_SECONDS = 60 * 60;
    private static final int START_IN_LAST_SECONDS = FETCH_FORECAST_INTERVAL_IN_SECONDS / 10;
    private static final String TAG = "WeatherifyApplication";

    private static WeatherifyApplication instance;

    private AppComponent appComponent;

    public WeatherifyApplication() {
        instance = this;
    }

    public static WeatherifyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        updateForecastData();
        schedulePeriodicUpdate();
    }

    public void updateForecastData() {
        Intent locationService = LocationManagerService.createIntent(
                getApplicationContext(),
                LocationManagerService.UPDATE_CURRENT_LOCATION
                        | LocationManagerService.UPDATE_FAV_LOCATIONS);
        startService(locationService);
    }

    private void schedulePeriodicUpdate() {
        Log.d(TAG, "schedulePeriodicUpdate: scheduling tasks");

        Task periodicTask = new PeriodicTask.Builder()
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setTag(GCMService.TAG_TASK_PERIODIC_LOG)
                .setService(GCMService.class)
                .setPeriod(FETCH_FORECAST_INTERVAL_IN_SECONDS)
                .setFlex(START_IN_LAST_SECONDS)
                .build();

        GcmNetworkManager gcmNetworkManager = GcmNetworkManager.getInstance(this);
        gcmNetworkManager.schedule(periodicTask);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
