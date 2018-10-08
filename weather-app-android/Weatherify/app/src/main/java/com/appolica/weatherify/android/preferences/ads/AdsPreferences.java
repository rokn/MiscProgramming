package com.appolica.weatherify.android.preferences.ads;

import android.content.SharedPreferences;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;
import com.appolica.weatherify.android.preferences.PreferencesModule;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Alexander Iliev on 22.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

@Scopes.Application
public class AdsPreferences {

    private static final String PREF_REMOVE_ADS_PURCHASE = "AdsFree";
    private SharedPreferences sharedPreferences;

    @Inject
    public AdsPreferences(
            @Named(PreferencesModule.ADS_SHARED_PREF) SharedPreferences sharedPreferences) {

        this.sharedPreferences = sharedPreferences;
    }

    public boolean adsRemoved() {
        return sharedPreferences.getBoolean(
                PREF_REMOVE_ADS_PURCHASE,
                false);
    }

    public void setAdsRemovedStatus(boolean purchased) {
        sharedPreferences.edit()
                .putBoolean(PREF_REMOVE_ADS_PURCHASE, purchased)
                .apply();
    }
}
