package com.appolica.weatherify.android.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;

import com.annimon.stream.function.FunctionalInterface;

/**
 * Created by Alexander Iliev on 09.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
public class Utils {
    public static float convertDpToPixels(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static<T extends View> void runOnPreDraw(T view, OnPreDrawListener<T> onPreDrawListener) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                final boolean proceed = onPreDrawListener.onPreDraw(view);

                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return proceed;
            }
        });
    }

    public static double randDouble(double min, double max) {
        return min + Math.random() * (max - min);
    }

    public static float valueXPercentageOfMax(float value, float max) {
        if (max == 0) {
            return 0;
        }
        return (100 * value) / max;
    }

    @FunctionalInterface
    public interface OnPreDrawListener<T extends View> {
        boolean onPreDraw(T view);
    }
}
