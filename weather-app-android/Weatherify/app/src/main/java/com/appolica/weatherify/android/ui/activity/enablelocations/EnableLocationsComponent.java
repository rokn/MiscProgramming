package com.appolica.weatherify.android.ui.activity.enablelocations;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;

import dagger.Subcomponent;

/**
 * Created by Alexander Iliev on 02.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

@Scopes.Context
@Subcomponent()
public interface EnableLocationsComponent {
    void inject(EnableLocationsActivity activity);
}
