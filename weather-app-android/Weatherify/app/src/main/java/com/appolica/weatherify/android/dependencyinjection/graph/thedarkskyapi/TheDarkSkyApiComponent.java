package com.appolica.weatherify.android.dependencyinjection.graph.thedarkskyapi;

/**
 * Created by Bogomil Kolarov on 01.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public interface TheDarkSkyApiComponent {
    TheDarkSkyApiModule.TheDarkSkyForecastApi getTheDarkSkyApi();
}
