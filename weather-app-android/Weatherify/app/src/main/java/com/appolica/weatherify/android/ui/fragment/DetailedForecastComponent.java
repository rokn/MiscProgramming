package com.appolica.weatherify.android.ui.fragment;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;

import dagger.Subcomponent;

/**
 * Created by Alexander Iliev on 29.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
@Scopes.Context
@Subcomponent()
public interface DetailedForecastComponent {
    void inject(DetailedForecastFragment fragment);
    void inject(DetailedForecastViewModel viewModel);
}
