package com.appolica.weatherify.android.service;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;

import dagger.Subcomponent;

/**
 * Created by Bogomil Kolarov on 01.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
@Scopes.Context
@Subcomponent()
public interface LocationManagerServiceComponent {
    void inject(LocationManagerService service);
}
