package com.appolica.weatherify.android.ui.view.layerimageview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Path;

import com.appolica.weatherify.android.ui.animation.SimpleAnimatorListener;
import com.appolica.weatherify.android.ui.view.layerimageview.drawer.complex.PathBmpDrawer;
import com.appolica.weatherify.android.ui.view.layerimageview.layer.complex.PathBmpLayer;

/**
 * Created by Bogomil Kolarov on 28.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public class LayerImageViewAnimations {

    private static final String TAG = "LayerImageViewAnimation";

    public static Animator getFillBottomToTopAnimation(
            final LayeredImageView layeredImageView,
            final int layerIndex,
            final float percentage) {

        final PathBmpDrawer drawer = (PathBmpDrawer) layeredImageView.getDrawers().get(layerIndex);

        return getFillBottomToTopAnimation(layeredImageView, drawer.getLayer(), percentage);
    }

    public static Animator getFillBottomToTopAnimation(
            final LayeredImageView layeredImageView,
            final PathBmpLayer layer,
            final float percentage) {

        if (layer.getPathLayer().getPath() == null) {
            layer.getPathLayer().setPath(new Path());
        }

        final ValueAnimator layerAnimation = ValueAnimator.ofFloat(0, percentage);

        layerAnimation.addListener(
                SimpleAnimatorListener.forStart(animation -> {
                    layer.getPathLayer().setShouldClipPath(true);
                    layer.getBmpLayer().setShouldDraw(true);
                }));

        layerAnimation.addUpdateListener(animation -> {
            final float animatedPercentage = (float) animation.getAnimatedValue();

            final float newHeight =
                    layeredImageView.getHeight()
                            - (layeredImageView.getHeight() * (animatedPercentage / 100));

            layer.getPathLayer()
                    .getPathConstructor()
                    .reset()
                    .moveTo(0, layeredImageView.getHeight())
                    .lineTo(0, newHeight)
                    .lineTo(layeredImageView.getWidth(), newHeight)
                    .lineTo(layeredImageView.getWidth(), layeredImageView.getHeight())
                    .close();

            layeredImageView.invalidate();
        });

        return layerAnimation;
    }

    public static Animator getFillLeftToRightAnimation(
            final LayeredImageView layeredImageView,
            final int layerIndex,
            final float percentage) {

        final PathBmpDrawer drawer = (PathBmpDrawer) layeredImageView.getDrawers().get(layerIndex);

        return getFillLeftToRightAnimation(layeredImageView, drawer.getLayer(), percentage);
    }

    public static Animator getFillLeftToRightAnimation(
            final LayeredImageView layeredImageView,
            final PathBmpLayer layer,
            final float percentage) {

        if (layer.getPathLayer().getPath() == null) {
            layer.getPathLayer().setPath(new Path());
        }

        final ValueAnimator layerAnimation = ValueAnimator.ofFloat(0, percentage);

        layerAnimation.addListener(
                SimpleAnimatorListener.forStart(animation -> {
                    layer.getPathLayer().setShouldClipPath(true);
                    layer.getBmpLayer().setShouldDraw(true);
                }));

        layerAnimation.addUpdateListener(animation -> {
            final float animatedPercentage = (float) animation.getAnimatedValue();
            final float newWidth = layeredImageView.getWidth() * (animatedPercentage / 100);

            layer.getPathLayer()
                    .getPathConstructor()
                    .reset()
                    .moveTo(0, 0)
                    .lineTo(newWidth, 0)
                    .lineTo(newWidth, layeredImageView.getHeight())
                    .lineTo(0, layeredImageView.getHeight())
                    .close();

            layeredImageView.invalidate();
        });


        return layerAnimation;
    }

}
