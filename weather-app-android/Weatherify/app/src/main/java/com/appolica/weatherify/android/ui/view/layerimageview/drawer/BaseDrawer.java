package com.appolica.weatherify.android.ui.view.layerimageview.drawer;

import android.animation.Animator;

public abstract class BaseDrawer implements LayerDrawer {
    private Animator layerAnimation;

    @Override
    public void startAnimation() {
        if (layerAnimation != null) {
            layerAnimation.start();
        }
    }

    public void setLayerAnimation(Animator layerAnimation) {
        this.layerAnimation = layerAnimation;
    }
}
