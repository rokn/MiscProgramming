package com.appolica.weatherify.android.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

public class ForecastAnimations {

    private static final String TAG = "ForecastAnimations";

    private static final int SECOND = 1000;
    private static final long ANIMATION_DURATION = 3 * SECOND;
    public static final String DRAWABLE_PROPERTY_LEVEL = "level";

    public Animator createHumidityAnimation(ImageView humidityView, float level) {
        final LayerDrawable drawable = (LayerDrawable) humidityView.getDrawable();
        return createClipAnimationFor(level, drawable);
    }

    public Animator createPrecipitationAnimation(ImageView precipitationView, float level) {
        final LayerDrawable drawable = (LayerDrawable) precipitationView.getDrawable();
        return createClipAnimationFor(level, drawable);
    }

    @NonNull
    private ObjectAnimator createClipAnimationFor(float level, LayerDrawable drawable) {
        final ClipDrawable clipDrawable = (ClipDrawable) drawable.getDrawable(1);

        final ObjectAnimator clipAnimation =
                ObjectAnimator.ofInt(clipDrawable, DRAWABLE_PROPERTY_LEVEL, 0, (int) (level * 10000));

        clipAnimation.setDuration((long) (ANIMATION_DURATION * level));
        return clipAnimation;
    }
}
