package com.appolica.weatherify.android.ui.activity.favourites;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.ui.activity.MainActivity;
import com.appolica.weatherify.android.ui.activity.Revealable;
import com.appolica.weatherify.android.ui.activity.addlocation.AddLocationActivityWithAnimation;
import com.appolica.weatherify.android.ui.animation.RevealAnimationBuilder;
import com.appolica.weatherify.android.utils.ProvidersUtils;

/**
 * Created by Alexander Iliev on 08.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
public class FavouritesActivityWithAnimation extends FavouritesActivity implements Revealable {
    public static final String CENTER_Y_EXTRA = "centerY";
    public static final String CENTER_X_EXTRA = "centerX";

    private View rootLayout;
    private RevealAnimationBuilder revealAnimationBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        revealAnimationBuilder = new RevealAnimationBuilder().setContext(this);

        rootLayout = binding.activityFavourites;
        revealAnimationBuilder.setRootLayout(rootLayout);

        if (savedInstanceState == null) {
            Intent intent = getIntent();

            rootLayout.setVisibility(View.INVISIBLE);

            if (intent.hasExtra(CENTER_X_EXTRA) && intent.hasExtra(CENTER_Y_EXTRA)) {
                revealAnimationBuilder
                        .setRevealCenterX(intent.getIntExtra(CENTER_X_EXTRA, 0))
                        .setRevealCenterY(intent.getIntExtra(CENTER_Y_EXTRA, 0));

                ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealAnimationBuilder.setChangeCallback(null);
                        transitionAnimation(true);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        rootLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void goToMainActivity() {
        revealAnimationBuilder.setChangeCallback(() -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
        transitionAnimation(false);
    }

    @Override
    public void onTextFieldClick() {
        if (ProvidersUtils.networkConnectionEstablished(this)) {
            Intent intent = new Intent(this, AddLocationActivityWithAnimation.class);
            intent.putExtra(AddLocationActivityWithAnimation.CONCEALABLE_EXTRA,
                    AddLocationActivityWithAnimation.CONCEALABLE);

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, R.anim.do_not_move);
            finish();
        } else {
            //todo mind this
            Toast.makeText(this, "need internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void transitionAnimation(boolean revealing) {
        int coords[] = new int[2];
        binding.favouritesCloseButton.getLocationOnScreen(coords);

        Animator revealAnimation = revealAnimationBuilder
                .setRevealing(revealing)
                .setCoordsOnScreen(coords[0], coords[1])
                .buildAnimation();

        if (revealAnimation != null) {
            if (!revealing) {
                binding.setClickListener(null);
            }

            revealAnimation.start();
        }
    }
}
