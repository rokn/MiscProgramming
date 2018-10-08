package com.appolica.weatherify.android.dependencyinjection.graph.location;

import android.content.Context;
import android.text.format.DateUtils;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import dagger.Module;
import dagger.Provides;

/**
 * Created by aleksandar
 */
@Module
public class LocationModule {
    @Provides
    LocationRequest provideLocationRequest() {

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(DateUtils.SECOND_IN_MILLIS * 2);

        return locationRequest;
    }

    @Provides
    @Scopes.Application
    GoogleApiClient provideGoogleApiClient(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();

        return googleApiClient;
    }

    @Provides
    @Scopes.Application
    FusedLocationProviderClient provideProviderClient(Context context) {
        return LocationServices.getFusedLocationProviderClient(context);
    }
}
