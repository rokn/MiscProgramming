package com.appolica.weatherify.android.ui.activity.enablelocations;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;
import android.widget.ImageView;

import com.appolica.weatherify.android.BR;
import com.appolica.weatherify.android.ui.animation.bouncyspring.BouncyRotationSpringAnimation;
import com.facebook.rebound.SpringSystem;

/**
 * Created by Alexander Iliev on 28.06.17.
 * Copyright © 2017 Appolica. All rights reserved.
 */
public class EnableLocationsViewModel extends BaseObservable implements EnableLocationsContract.ViewModel {

    private static final String TAG = "EnableLocationsViewMode";

    private int hideSettingsButton;
    private EnableLocationsContract.Controller controller;

    private BouncyRotationSpringAnimation arrowAnimation;
    private SpringSystem springSystem;

    public EnableLocationsViewModel(EnableLocationsContract.Controller controller) {
        this.controller = controller;
        springSystem = SpringSystem.create();
    }

    public void animateCompassArrow(ImageView view) {
        final Runnable createAnimation = () -> {
            final int width = view.getWidth();
            final int height = view.getHeight();

            view.setPivotX(width / 2);
            view.setPivotY(height / 2);

            final double minDegrees = 40d;
            final double maxDegrees = 130d;

            arrowAnimation = BouncyRotationSpringAnimation.instance(
                    springSystem,
                    view.getRotation(),
                    minDegrees, maxDegrees,
                    view::setRotation);

            arrowAnimation.start();
        };

        if (arrowAnimation != null) {
            arrowAnimation.addStopListener(animation -> {
                createAnimation.run();
            });
            arrowAnimation.stop();
        } else {
            createAnimation.run();
        }
    }

    public void stopArrowAnimation() {
        if (arrowAnimation != null) {
            arrowAnimation.stop();
            arrowAnimation = null;
        }
    }

    @Bindable
    public int getHideSettingsButton() {
        return hideSettingsButton;
    }

    public void setHideSettingsButton(boolean hasLocationPermissions) {
        hideSettingsButton = hasLocationPermissions ? View.INVISIBLE : View.VISIBLE;
        notifyPropertyChanged(BR.hideSettingsButton);
    }

    @Override
    public void onGoToFavourites() {
        controller.onGoToFavourites();
    }

    @Override
    public void onGoToSettings() {
        controller.onGoToSettings();
    }
}
