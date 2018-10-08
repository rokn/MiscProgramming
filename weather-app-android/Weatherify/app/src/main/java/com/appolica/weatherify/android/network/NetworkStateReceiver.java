package com.appolica.weatherify.android.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Iliev on 06.12.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    private ConnectivityManager mManager;
    private List<NetworkStateReceiverListener> mListeners;
    private boolean mConnected;

    public NetworkStateReceiver(Context context) {
        mListeners = new ArrayList<>();
        mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        checkStateChanged();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            return;
        }

        if (checkStateChanged()) {
            notifyStateToAll();
        }
    }

    private boolean checkStateChanged() {
        boolean prev = mConnected;

        NetworkInfo activeNetwork = mManager.getActiveNetworkInfo();
        mConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return prev != mConnected;
    }

    private void notifyStateToAll() {
        for (NetworkStateReceiverListener listener : mListeners) {
            notifyState(listener);
        }
    }

    private void notifyState(NetworkStateReceiverListener listener) {
        if (listener != null) {
            if (mConnected) {
                listener.onNetworkAvailable();
            } else {
                listener.onNetworkUnavailable();
            }
        }
    }

    public void addListener(NetworkStateReceiverListener listener) {
        mListeners.add(listener);
        notifyState(listener);
    }

    public void removeListener(NetworkStateReceiverListener listener) {
        mListeners.remove(listener);
    }

    public interface NetworkStateReceiverListener {
        void onNetworkAvailable();

        void onNetworkUnavailable();
    }
}
