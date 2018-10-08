package com.appolica.weatherify.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;


public class LocationProvidersChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "ProvidersChangedReceive";

    public static IntentFilter getFilter() {
        return new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            final Intent serviceIntent = LocationManagerService.createIntent(context, LocationManagerService.UPDATE_CURRENT_LOCATION);
            context.startService(serviceIntent);
        }
    }
}
