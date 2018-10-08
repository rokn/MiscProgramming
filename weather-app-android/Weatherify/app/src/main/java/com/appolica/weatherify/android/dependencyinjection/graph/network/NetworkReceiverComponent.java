package com.appolica.weatherify.android.dependencyinjection.graph.network;

import com.appolica.weatherify.android.network.NetworkStateReceiver;

/**
 * Created by Alexander Iliev on 19.05.17.
 * Copyright © 2017 Appolica. All rights reserved.
 */

public interface NetworkReceiverComponent {
    NetworkStateReceiver getNewworkNetworkStateReceiver();
}
