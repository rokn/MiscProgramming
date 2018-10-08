package com.appolica.weatherify.android.utils;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.View;
import android.view.animation.Interpolator;

/**
 * Created by Bogomil Kolarov on 01.12.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public class Animations {

    private static final String TAG = "Animations";

    public static ValueAnimator createOrbitAnimation(@NonNull final View orbitingView,
                                                     @NonNull final View orbitedView,
                                                     long percentage) {

        final float margin = Math.abs(orbitedView.getX() - (orbitingView.getX() + orbitingView.getWidth()));

        // If view bottoms aren't aligned
        final float orbitBottom = orbitedView.getY() + orbitedView.getHeight();
        final float orbitedBottom = orbitingView.getY() + orbitingView.getHeight();
        final float dBottom = Math.abs(orbitBottom - orbitedBottom);

        final float left = 0;
        final float top = -(orbitedView.getHeight() + margin + dBottom);
        final float right = left + 2 * margin + orbitingView.getWidth() + orbitedView.getWidth();
        final float bottom = (orbitedView.getHeight() + margin + dBottom);

        final RectF oval = new RectF(left, top, right, bottom);
        final Path path = new Path();

        final float sweepAngle = 180f * (percentage / 100f);
        path.arcTo(oval, 180f, Math.min(180f, sweepAngle));

        final PathMeasure pathMeasure = new PathMeasure(path, false);
        final float[] point = new float[2];
        final float pathLength = pathMeasure.getLength();

        final ValueAnimator valueAnimator;
        valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimator.addUpdateListener(animation -> {

            final float animatedValue = (float) animation.getAnimatedValue();
            final float distance = pathLength * animatedValue;
            pathMeasure.getPosTan(distance, point, null);

            orbitingView.setTranslationX(point[0]);
            orbitingView.setTranslationY(point[1]);
        });

        return valueAnimator;
    }

    @NonNull
    public static Interpolator getEaseInOutInterpolator() {
        // An interpolator, that uses Bezier curve. The control points are
        // from the iOS kCAMediaTimingFunctionEaseInEaseOut timing function
        return PathInterpolatorCompat.create(0.42f, 0.0f, 0.58f, 1.0f);
    }

    @NonNull
    public static ValueAnimator.AnimatorUpdateListener getColorEvaluator(Drawable drawable, int fromColor, int toColor) {
        final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        return animation -> {
            final float fraction = animation.getAnimatedFraction();

            final int color = (int) argbEvaluator.evaluate(fraction, fromColor, toColor);

            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        };
    }

    @NonNull
    public static ValueAnimator.AnimatorUpdateListener getColorEvaluatorToPercentage(
            Drawable drawable,
            int startColor,
            int endColor,
            int percentage) {

        final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        final int endPercentageColor = (int) argbEvaluator.evaluate(percentage / 100f, startColor, endColor);

        return getColorEvaluator(drawable, startColor, endPercentageColor);
    }
}
