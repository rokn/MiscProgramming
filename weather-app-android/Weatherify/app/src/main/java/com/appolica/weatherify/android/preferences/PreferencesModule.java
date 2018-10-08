package com.appolica.weatherify.android.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Bogomil Kolarov on 03.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

@Module
public class PreferencesModule {

    public static final String SETTINGS_SHARED_PREF = "Settings";
    public static final String ADS_SHARED_PREF = "Ads";
    public static final String PERMISSIONS_SHARED_PREF = "Permissions";

    @Scopes.Application
    @Provides
    @Named(PreferencesModule.SETTINGS_SHARED_PREF)
    public SharedPreferences provideSettingsSharedPreferences(Context context) {
        return context.getSharedPreferences(SETTINGS_SHARED_PREF, Context.MODE_PRIVATE);
    }

    @Scopes.Application
    @Provides
    @Named(PreferencesModule.ADS_SHARED_PREF)
    public SharedPreferences provideAdsSharedPreferences(Context context) {
        return context.getSharedPreferences(ADS_SHARED_PREF, Context.MODE_PRIVATE);
    }

    @Scopes.Application
    @Provides
    @Named(PreferencesModule.PERMISSIONS_SHARED_PREF)
    public SharedPreferences providePermissionsSharedPreferences(Context context) {
        return context.getSharedPreferences(PERMISSIONS_SHARED_PREF, Context.MODE_PRIVATE);
    }
}
