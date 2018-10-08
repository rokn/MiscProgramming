package com.appolica.weatherify.android.ui.refresh;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.appolica.weatherify.android.R;
import com.gelitenight.waveview.library.WaveView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Iliev on 09.04.17.
 * Copyright © 2017 Appolica. All rights reserved.
 */
class WaveHelper {
    private static final float WAVE_HORIZONTAL_SHIFT_MIN = 0f;
    private static final float WAVE_HORIZONTAL_SHIFT_MAX = 2f;
    private static final float WAVE_VERTICAL_LEVEL_MIN = 0f;
    private static final float WAVE_VERTICAL_LEVEL_MAX = 1.1f;
    private static final float WAVE_AMPLITUDE_MIN = 0.0001f;
    private static final float WAVE_AMPLITUDE_MAX = 0.1f;

    private int waveDuration;
    private WaveView mWaveView;
    private AnimatorSet mAnimatorSet;

    WaveHelper(WaveView waveView) {
        mWaveView = waveView;
        waveDuration = waveView.getContext().getResources()
                .getInteger(R.integer.refreshWaveDuration);
        initAnimation();
    }

    public void start() {
        mWaveView.setShowWave(true);
        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    void addAnimationListener(Animator.AnimatorListener animatorListener) {
        mAnimatorSet.addListener(animatorListener);
    }

    private void initAnimation() {
        List<Animator> animators = new ArrayList<>();

        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(
                mWaveView, "waveShiftRatio", WAVE_HORIZONTAL_SHIFT_MIN, WAVE_HORIZONTAL_SHIFT_MAX);
        waveShiftAnim.setDuration(waveDuration);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        animators.add(waveShiftAnim);

        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(
                mWaveView, "waterLevelRatio", WAVE_VERTICAL_LEVEL_MIN, WAVE_VERTICAL_LEVEL_MAX);
        waterLevelAnim.setDuration(waveDuration);
        waterLevelAnim.setInterpolator(new AccelerateInterpolator());
        animators.add(waterLevelAnim);

        ObjectAnimator amplitudeAnim = ObjectAnimator.ofFloat(
                mWaveView, "amplitudeRatio", WAVE_AMPLITUDE_MIN, WAVE_AMPLITUDE_MAX);
        amplitudeAnim.setDuration(waveDuration);
        amplitudeAnim.setInterpolator(new LinearInterpolator());
        animators.add(amplitudeAnim);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animators);
    }
}
