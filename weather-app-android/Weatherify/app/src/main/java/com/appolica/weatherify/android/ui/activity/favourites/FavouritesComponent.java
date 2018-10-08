package com.appolica.weatherify.android.ui.activity.favourites;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;

import dagger.Subcomponent;

/**
 * Created by Bogomil Kolarov on 01.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

@Scopes.Context
@Subcomponent()
public interface FavouritesComponent {
    void inject(FavouritesActivity activity);
}
