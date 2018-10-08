package com.appolica.weatherify.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.appolica.weatherify.android.WeatherifyApplication;
import com.appolica.weatherify.android.db.DBManager;
import com.appolica.weatherify.android.dependencyinjection.graph.app.AppComponent;
import com.appolica.weatherify.android.dependencyinjection.graph.thedarkskyapi.TheDarkSkyApiModule;
import com.appolica.weatherify.android.model.Forecast;
import com.appolica.weatherify.android.model.LocationCoordinates;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class FetchForecastIntentService extends IntentService {
    public static final String EXTRA_LOCATION = "extraLocation";

    private static final String TAG = "FFIS";
    private static final String FLAGS_PARAMETER = "flags,alerts,minutes";
    private static final String UNITS_PARAMETER = "si";


    @Inject
    TheDarkSkyApiModule.TheDarkSkyForecastApi forecastApi;
    @Inject
    DBManager dbManager;

    public FetchForecastIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final WeatherifyApplication application = (WeatherifyApplication) getApplication();
        final AppComponent appComponent = application.getAppComponent();

        final ForecastServiceComponent serviceComponent =
                appComponent.getForecastServiceComponent();

        serviceComponent.inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager.releaseHelper();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        if (intent != null) {
            LocationCoordinates locationCoordinates =
                    (LocationCoordinates) intent.getSerializableExtra(EXTRA_LOCATION);

            if (locationCoordinates == null) {
                throw new IllegalStateException("LocationCoordinates serializable must be provided!");
            }

            Response<Forecast> response;
            try {
                final Call<Forecast> forecastRequest =
                        forecastApi.getForecast(
                                locationCoordinates.getLocationString(this),
                                UNITS_PARAMETER,
                                FLAGS_PARAMETER
                        );

                response = forecastRequest.execute();

                Log.d(TAG, response.raw().toString());

                dbManager.updateForecastForLocation(response.body(), locationCoordinates);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
