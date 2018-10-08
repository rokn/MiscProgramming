package com.appolica.weatherify.android.preferences.settings;

import android.content.SharedPreferences;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;
import com.appolica.weatherify.android.preferences.PreferencesModule;
import com.appolica.weatherify.android.preferences.units.LocalizedUnits;
import com.appolica.weatherify.android.preferences.units.PreferredUnits;
import com.appolica.weatherify.android.preferences.units.SpeedUnit;
import com.appolica.weatherify.android.preferences.units.TemperatureUnit;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Bogomil Kolarov on 04.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

@Scopes.Application
public class SettingsPreferences {
    private static final String PREF_TEMPERATURE_UNIT = "TemperatureUnit";
    private static final String PREF_WIND_UNIT = "SpeedUnit";

    private SharedPreferences sharedPreferences;
    private PreferredUnits preferredUnits;

    @Inject
    public SettingsPreferences(
            @Named(PreferencesModule.SETTINGS_SHARED_PREF) SharedPreferences sharedPreferences) {

        this.sharedPreferences = sharedPreferences;
        this.preferredUnits = new PreferredUnits(getTemperatureUnit(), getSpeedUnit());
    }

    public PreferredUnits getPreferredUnits() {
        return preferredUnits;
    }

    public TemperatureUnit getTemperatureUnit() {

        final String temperatureUnitName =
                sharedPreferences.getString(
                        PREF_TEMPERATURE_UNIT,
                        LocalizedUnits.getDefaultTemperatureUnit().name());

        return TemperatureUnit.valueOf(temperatureUnitName);
    }

    public SpeedUnit getSpeedUnit() {

        final String windUnitName =
                sharedPreferences.getString(
                        PREF_WIND_UNIT,
                        LocalizedUnits.getDefaultWindUnit().name());

        return SpeedUnit.valueOf(windUnitName);
    }

    public void updatePreferredUnits(PreferredUnits preferredUnits) {
        this.preferredUnits.updateFrom(preferredUnits);
        sharedPreferences.edit()
            .putString(PREF_WIND_UNIT, preferredUnits.getSpeedUnit().name())
            .putString(PREF_TEMPERATURE_UNIT, preferredUnits.getTemperatureUnit().name())
            .apply();
    }
}
