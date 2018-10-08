package com.appolica.weatherify.android.location;

import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.Task;

import java.util.LinkedList;
import java.util.Queue;

import javax.inject.Inject;

public class AppLocationManager extends LocationCallback
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "AppLocationManager";
    public static final boolean CONNECTION_SUCCESS = true;
    public static final boolean CONNECTION_FAILED = false;

    @Inject
    GoogleApiClient gapiClient;
    @Inject
    LocationRequest locationRequest;
    @Inject
    FusedLocationProviderClient locationProviderClient;

    @Inject
    public AppLocationManager() {

    }

    private Queue<ArgTask<Boolean>> connectionQueue = new LinkedList<>();
    private Queue<ArgTask<Result<Location>>> locationQueue = new LinkedList<>();

    public void connect() {
        Log.d(TAG, "GAPI: connecting...");
        gapiClient.registerConnectionCallbacks(this);
        gapiClient.registerConnectionFailedListener(this);
        gapiClient.connect();
    }

    public void disconnect() {
        Log.d(TAG, "GAPI: disconnect");
        gapiClient.disconnect();
    }

    @SuppressWarnings("MissingPermission")
    @RequiresPermission(
            anyOf = {
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_FINE_LOCATION"
            })
    public void getLocation(AppLocationCallback callback) {
        if (gapiClient.isConnected()) {
            Log.d(TAG, "getLocation: connected");
            locationQueue.add(result -> {
                if (result.isSuccessful()) {
                    callback.onLocationGet(result.arg);
                } else {
                    callback.onLocationGetError();
                }
            });

            requestUpdates(this);
        } else {
            Log.d(TAG, "getLocation: Not connected to GAPI. Enqueuing getLocation() task.");
            connectionQueue.add(success -> {
                    if (success)
                        getLocation(callback);
                    else
                        callback.onLocationGetError();
            });

            if (!gapiClient.isConnecting()) {
                Log.d(TAG, "getLocation: Connecting to GAPI...");
                connect();
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    @RequiresPermission(
            anyOf = {
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_FINE_LOCATION"
            })
    public void requestUpdates(LocationCallback locationCallback) {
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: Connection to GAPI successful. Running enqueued tasks.");
        while (!connectionQueue.isEmpty()) {
            connectionQueue.poll().run(CONNECTION_SUCCESS);
        }
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        locationProviderClient.removeLocationUpdates(this);

        while (!locationQueue.isEmpty()) {
            locationQueue.poll().run(new Result<>(true, locationResult.getLastLocation()));
        }
    }

    @Override
    public void onLocationAvailability(LocationAvailability locationAvailability) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        while (!connectionQueue.isEmpty()) {
            connectionQueue.poll().run(CONNECTION_FAILED);
        }
    }

    private interface ArgTask<T> {
        void run(T arg);
    }

    private class Result<T> {
        private final boolean isSuccessful;
        private final T arg;

        public Result(boolean isSuccessful, T arg) {
            this.isSuccessful = isSuccessful;
            this.arg = arg;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }

        public T getArg() {
            return arg;
        }
    }

    public interface AppLocationCallback {
        void onLocationGet(Location location);
        void onLocationGetError();
    }
}
