package com.appolica.weatherify.android.ui.animation;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.appolica.weatherify.android.R;

public class RevealAnimationBuilder {
    private Context context;
    private View rootLayout;
    private boolean revealing;
    private Animator revealAnimation;
    private int revealCenterX;
    private int revealCenterY;
    private int[] coordsOnScreen;
    private ActivityChangeCallback changeCallback;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Animator buildAnimation() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (revealing) {
                rootLayout.setVisibility(View.VISIBLE);
            } else {
                if(changeCallback != null) {
                    changeCallback.changeActivity();
                }
            }
            return null;
        }

        if (revealAnimation != null && revealAnimation.getListeners() != null) {
            return null;
        }

        if (revealing) {
            int revealPointHeight = Math.max(rootLayout.getHeight() - revealCenterY, revealCenterY);
            int revealPointWidth = Math.max(rootLayout.getWidth() - revealCenterX, revealCenterX);

            float revealCollapsedRadius = 0;
            float revealExpandedRadius = (float) Math.hypot(revealPointWidth, revealPointHeight);

            revealAnimation = ViewAnimationUtils.createCircularReveal(
                    rootLayout, revealCenterX, revealCenterY, revealCollapsedRadius, revealExpandedRadius
            );
        } else {

            int concealCenterX = coordsOnScreen[0] +
                    (int) context.getResources().getDimension(R.dimen.top_buttons_side_padding) +
                    (int) context.getResources().getDimension(R.dimen.top_buttons_image_size) / 2;
            int concealCenterY = coordsOnScreen[1] +
                    (int) context.getResources().getDimension(R.dimen.top_buttons_image_size) / 2;

            int concealPointHeight = Math.max(rootLayout.getHeight() - concealCenterY, concealCenterY);
            int concealPointWidth = Math.max(rootLayout.getWidth() - concealCenterX, concealCenterX);
            float concealCollapsedRadius = 0;
            float concealExpandedRadius = (float) Math.hypot(concealPointWidth, concealPointHeight);

            revealAnimation = ViewAnimationUtils.createCircularReveal(
                    rootLayout, concealCenterX, concealCenterY, concealExpandedRadius, concealCollapsedRadius
            );
        }

        revealAnimation.setDuration(context.getResources().getInteger(R.integer.revealConcealAnimationDuration));

        if (revealing) {
            rootLayout.setVisibility(View.VISIBLE);
        } else {
            revealAnimation.addListener(new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            rootLayout.setVisibility(View.INVISIBLE);
                            if(changeCallback != null) {
                                changeCallback.changeActivity();
                            }
                        }
                    }
);
        }

        return revealAnimation;
    }

    public RevealAnimationBuilder setRevealCenterY(int revealCenterY) {
        this.revealCenterY = revealCenterY;
        return this;
    }

    public RevealAnimationBuilder setRevealCenterX(int revealCenterX) {
        this.revealCenterX = revealCenterX;
        return this;
    }

    public RevealAnimationBuilder setRevealing(boolean revealing) {
        this.revealing = revealing;
        return this;
    }

    public RevealAnimationBuilder setRootLayout(View rootLayout) {
        this.rootLayout = rootLayout;
        return this;
    }

    public RevealAnimationBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    public RevealAnimationBuilder setCoordsOnScreen(int x, int y) {
        this.coordsOnScreen = new int[2];
        this.coordsOnScreen[0] = x;
        this.coordsOnScreen[1] = y;
        return this;
    }

    public RevealAnimationBuilder setChangeCallback(ActivityChangeCallback changeCallback) {
        this.changeCallback = changeCallback;
        return this;
    }

    public interface ActivityChangeCallback {
        void changeActivity();
    }
}
