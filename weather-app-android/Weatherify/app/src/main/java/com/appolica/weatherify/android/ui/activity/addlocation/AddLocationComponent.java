package com.appolica.weatherify.android.ui.activity.addlocation;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;

import dagger.Subcomponent;

/**
 * Created by Bogomil Kolarov on 24.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
@Scopes.Context
@Subcomponent()
public interface AddLocationComponent {
    void inject(AddLocationActivity activity);
}
