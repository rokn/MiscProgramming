package com.appolica.weatherify.android.ui.activity.settings;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;

import dagger.Subcomponent;

/**
 * Created by Bogomil Kolarov on 08.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

@Scopes.Context
@Subcomponent()
public interface SettingsComponent {
    void inject(SettingsActivity activity);
}
