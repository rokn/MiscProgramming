package com.appolica.weatherify.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.WeatherifyApplication;
import com.appolica.weatherify.android.db.DBManager;
import com.appolica.weatherify.android.dependencyinjection.graph.app.AppComponent;
import com.appolica.weatherify.android.location.AppLocationManager;
import com.appolica.weatherify.android.model.ForecastLocation;
import com.appolica.weatherify.android.model.LocationCoordinates;
import com.appolica.weatherify.android.utils.ProvidersUtils;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class LocationManagerService extends Service
        implements AppLocationManager.AppLocationCallback {

    public static final int UPDATE_CURRENT_LOCATION = 0b0001;
    public static final int UPDATE_FAV_LOCATIONS = 1 << 1;
    public static final int UPDATE_NO_OPTS = 0b0000;

    private static final String TAG = "LocationManagerService";
    private static final String EXTRA_UPDATE_OPTS = "update options";

    @Inject
    DBManager dbManager;
    @Inject
    AppLocationManager locationManager;

    public static Intent createIntent(Context context, @LocationServiceOpts int opts) {
        final Intent intent = new Intent(context, LocationManagerService.class);
        intent.putExtra(EXTRA_UPDATE_OPTS, opts);

        return intent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

        final WeatherifyApplication application =
                (WeatherifyApplication) getApplication();

        final AppComponent appComponent = application.getAppComponent();

        final LocationManagerServiceComponent component =
                appComponent.getLocationManagerServiceComponent();

        component.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            int runOpts = intent.getIntExtra(EXTRA_UPDATE_OPTS, UPDATE_NO_OPTS);

            if ((runOpts & UPDATE_FAV_LOCATIONS) == UPDATE_FAV_LOCATIONS) {
                updateFavouriteForecasts();
            }

            if ((runOpts & UPDATE_CURRENT_LOCATION) == UPDATE_CURRENT_LOCATION) {
                if (ProvidersUtils.checkLocationPermission(this)
                        && ProvidersUtils.locationProvidersEnabled(this)
                        && ProvidersUtils.networkConnectionEstablished(this)) {

                    locationManager.getLocation(this);
                } else {
                    stopSelf();
                }
            }

            if ((runOpts & 0b1111) == UPDATE_NO_OPTS) {
                stopSelf();
            }
        } else {
            Log.d(TAG, "onStartCommand: empty Intent, something is wrong");
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLocationGet(Location location) {
        final LocationCoordinates locationCoordinates =
                new LocationCoordinates(location.getLatitude(), location.getLongitude());
        locationCoordinates.setName(getLocationName(location.getLatitude(), location.getLongitude()));
        locationCoordinates.setCurrentLocaiton(true);

        updateCurrentLocation(locationCoordinates);

        startFetchService(locationCoordinates);
        stopSelf();
    }

    @Override
    public void onLocationGetError() {

    }

    private void updateFavouriteForecasts() {
        List<ForecastLocation> forecastLocations = dbManager.getFavoriteForecastLocations();
        for (ForecastLocation forecastLocation : forecastLocations) {
            LocationCoordinates locationCoordinates =
                    new LocationCoordinates(forecastLocation.getLatitude(), forecastLocation.getLongitude());
            locationCoordinates.setName(forecastLocation.getName());
            locationCoordinates.setLocationId(forecastLocation.getGoogleLocationId());

            startFetchService(locationCoordinates);
        }
    }

    private void startFetchService(LocationCoordinates locationCoordinates) {
        Intent intent = new Intent(getApplicationContext(), FetchForecastIntentService.class);
        intent.putExtra(FetchForecastIntentService.EXTRA_LOCATION, locationCoordinates);
        startService(intent);
    }

    private void updateCurrentLocation(LocationCoordinates locationCoordinates) {
        dbManager.updateCurrentLocation(locationCoordinates);
    }

    private String getLocationName(double lat, double lng) {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getLocality();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getString(R.string.unknown_current_location);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");

        dbManager.releaseHelper();
        locationManager.disconnect();
    }

    @IntDef(
            flag = true,
            value = {
                    UPDATE_CURRENT_LOCATION, UPDATE_FAV_LOCATIONS, UPDATE_NO_OPTS
            })
    public @interface LocationServiceOpts {
    }
}
