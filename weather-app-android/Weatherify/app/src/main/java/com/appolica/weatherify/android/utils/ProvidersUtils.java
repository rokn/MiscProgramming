package com.appolica.weatherify.android.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import permissions.dispatcher.PermissionUtils;

/**
 * Created by Alexander Iliev on 04.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
public class ProvidersUtils {
    public static final String[] PERMISSIONS_FOR_LOCATION =
            {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"};

    public static boolean checkLocationPermission(Context context) {
        return PermissionUtils.hasSelfPermissions(context, PERMISSIONS_FOR_LOCATION);
    }

    public static boolean locationProvidersEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsProviderEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkProviderEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return gpsProviderEnabled || networkProviderEnabled;
    }

    public static boolean networkConnectionEstablished(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean hasAllSettingsEnabled(Context context) {
        return checkLocationPermission(context)
                && locationProvidersEnabled(context)
                && networkConnectionEstablished(context);
    }
}
