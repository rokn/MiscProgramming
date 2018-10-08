package com.appolica.weatherify.android.dependencyinjection.graph.network;

import retrofit2.Retrofit;

/**
 * Created by Bogomil Kolarov on 01.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public interface NetworkComponent {
    Retrofit getRetrofit();
}
