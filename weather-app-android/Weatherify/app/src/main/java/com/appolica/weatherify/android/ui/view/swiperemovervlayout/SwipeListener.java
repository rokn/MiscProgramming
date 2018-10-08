package com.appolica.weatherify.android.ui.view.swiperemovervlayout;

import android.view.View;

/**
 * Created by Bogomil Kolarov on 13.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
public interface SwipeListener {
    void onStartSwipeAway(View swipeableView);

    void onSwiped(View swipeableView);

    void onStartReturnToIdle(View swipeableView);

    void onIdle(View swipeableView);
}
