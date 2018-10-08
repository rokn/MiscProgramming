package com.appolica.weatherify.android.ui.animation;

import android.animation.Animator;

public class SimpleAnimatorListener implements Animator.AnimatorListener {

    public static Animator.AnimatorListener forStart(final OnAnimationStartListener startListener) {
        return new SimpleAnimatorListener() {
            @Override
            public void onAnimationStart(final Animator animation) {
                startListener.onAnimationStart(animation);
            }
        };
    }

    public static Animator.AnimatorListener forEnd(final OnAnimationEndListener endListener) {
        return new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                endListener.onAnimationEnd(animation);
            }
        };
    }

    public static Animator.AnimatorListener forCancel(final OnAnimationCancelListener cancelListener) {
        return new SimpleAnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
                cancelListener.onAnimationCancel(animation);
            }
        };
    }

    public static Animator.AnimatorListener forRepeat(final OnAnimationRepeatListener repeatListener) {
        return new SimpleAnimatorListener() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                repeatListener.onAnimationRepeat(animation);
            }
        };
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public interface OnAnimationStartListener {
        public void onAnimationStart(Animator animator);
    }

    public interface OnAnimationEndListener {
        public void onAnimationEnd(Animator animation);
    }

    public interface OnAnimationCancelListener {
        void onAnimationCancel(Animator animation);
    }

    public interface OnAnimationRepeatListener {
        void onAnimationRepeat(Animator animation);
    }
}
