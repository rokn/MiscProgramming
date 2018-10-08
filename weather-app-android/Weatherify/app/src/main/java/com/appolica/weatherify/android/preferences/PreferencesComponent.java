package com.appolica.weatherify.android.preferences;

import com.appolica.weatherify.android.preferences.ads.AdsPreferences;
import com.appolica.weatherify.android.preferences.permissions.PermissionsPreferences;
import com.appolica.weatherify.android.preferences.settings.SettingsPreferences;

/**
 * Created by Bogomil Kolarov on 03.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public interface PreferencesComponent {
    SettingsPreferences getSettingsPreferences();

    AdsPreferences getAdsPreferences();

    PermissionsPreferences getPermissionsPreferences();
}
