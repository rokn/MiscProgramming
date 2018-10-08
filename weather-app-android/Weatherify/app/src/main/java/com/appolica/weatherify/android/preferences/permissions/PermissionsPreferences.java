package com.appolica.weatherify.android.preferences.permissions;

import android.content.SharedPreferences;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;
import com.appolica.weatherify.android.preferences.PreferencesModule;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Alexander Iliev on 08.06.17.
 * Copyright © 2017 Appolica. All rights reserved.
 */

@Scopes.Application
public class PermissionsPreferences {
    private static final String PREF_SETTINGS_HIDDEN_ONCE = "NeverAskAgain";

    private SharedPreferences sharedPreferences;

    @Inject
    public PermissionsPreferences(
            @Named(PreferencesModule.PERMISSIONS_SHARED_PREF) SharedPreferences sharedPreferences) {

        this.sharedPreferences = sharedPreferences;
    }

    public boolean shouldShowSettings() {
        return sharedPreferences.getBoolean(
                PREF_SETTINGS_HIDDEN_ONCE,
                false);
    }

    public void setSettingsHiddenOnce(boolean hidden) {
        sharedPreferences.edit()
                .putBoolean(PREF_SETTINGS_HIDDEN_ONCE, hidden)
                .apply();
    }
}
