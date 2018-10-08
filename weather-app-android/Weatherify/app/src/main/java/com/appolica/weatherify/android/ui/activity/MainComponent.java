package com.appolica.weatherify.android.ui.activity;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;

import dagger.Subcomponent;

/**
 * Created by Alexander Iliev on 29.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
@Scopes.Context
@Subcomponent()
public interface MainComponent {
    void inject(MainActivity activity);
}