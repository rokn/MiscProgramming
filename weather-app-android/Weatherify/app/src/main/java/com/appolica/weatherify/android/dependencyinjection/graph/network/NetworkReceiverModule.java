package com.appolica.weatherify.android.dependencyinjection.graph.network;

import android.content.Context;

import com.appolica.weatherify.android.dependencyinjection.scopes.Scopes;
import com.appolica.weatherify.android.network.NetworkStateReceiver;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Alexander Iliev on 19.05.17.
 * Copyright © 2017 Appolica. All rights reserved.
 */
@Module
public class NetworkReceiverModule {
    @Provides
    @Scopes.Application
    NetworkStateReceiver provideNetworkStateReceiver(Context context) {
        return new NetworkStateReceiver(context);
    }
}
