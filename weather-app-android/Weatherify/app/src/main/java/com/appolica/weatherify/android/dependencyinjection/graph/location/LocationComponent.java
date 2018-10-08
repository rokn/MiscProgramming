package com.appolica.weatherify.android.dependencyinjection.graph.location;

import com.appolica.weatherify.android.location.AppLocationManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by Bogomil Kolarov on 01.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public interface LocationComponent {
    GoogleApiClient getGoogleApiClient();

    LocationRequest getLocationRequest();

    AppLocationManager getLocationManager();
}
