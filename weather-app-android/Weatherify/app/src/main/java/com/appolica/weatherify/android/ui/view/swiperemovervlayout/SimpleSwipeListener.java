package com.appolica.weatherify.android.ui.view.swiperemovervlayout;

import android.view.View;

/**
 * Created by Bogomil Kolarov on 19.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public class SimpleSwipeListener implements SwipeListener {

    public static SimpleSwipeListener forStart(OnStartSwipeListener swipeListener) {
        return new SimpleSwipeListener() {
            @Override
            public void onStartSwipeAway(View swipeableView) {
                swipeListener.onStartSwipeAway(swipeableView);
            }
        };
    }

    public static SimpleSwipeListener forSwiped(OnSwipedListener swipedListener) {
        return new SimpleSwipeListener() {
            @Override
            public void onSwiped(View swipeableView) {
                swipedListener.onSwiped(swipeableView);
            }
        };
    }

    @Override
    public void onStartSwipeAway(View swipeableView) {

    }

    @Override
    public void onSwiped(View swipeableView) {

    }

    @Override
    public void onStartReturnToIdle(View swipeableView) {

    }

    @Override
    public void onIdle(View swipeableView) {

    }

    public interface OnStartSwipeListener {
        void onStartSwipeAway(View swipeableView);
    }

    public interface OnSwipedListener {
        void onSwiped(View swipeableView);
    }
}
