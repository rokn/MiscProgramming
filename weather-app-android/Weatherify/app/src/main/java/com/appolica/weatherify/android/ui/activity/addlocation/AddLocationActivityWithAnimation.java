package com.appolica.weatherify.android.ui.activity.addlocation;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.ui.activity.MainActivity;
import com.appolica.weatherify.android.ui.activity.Revealable;
import com.appolica.weatherify.android.ui.activity.favourites.FavouritesActivityWithAnimation;
import com.appolica.weatherify.android.ui.animation.RevealAnimationBuilder;

/**
 * Created by Alexander Iliev on 02.06.17.
 * Copyright © 2017 Appolica. All rights reserved.
 */
public class AddLocationActivityWithAnimation extends AddLocationActivity implements Revealable {
    private static final String TAG = "AnimatedAddLocation";

    public static final boolean REVEALING = true;


    public static final String CONCEALABLE_EXTRA = "concealable";
    public static final boolean CONCEALABLE = true;

    private RevealAnimationBuilder revealAnimationBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootLayout = binding.activityAddLocation;
        revealAnimationBuilder = new RevealAnimationBuilder();
        revealAnimationBuilder.setRootLayout(rootLayout).setContext(this);
    }

    @Override
    protected void continueToMainActivity(String locationId) {
        revealAnimationBuilder
                .setChangeCallback(() -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra(MainActivity.WAIT_FOR_LOCATION, true);
                    intent.putExtra(MainActivity.GOTO_LOCATION_ID, locationId);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });
        transitionAnimation(!REVEALING);
    }

    @Override
    protected void returnToFavourites() {
        Intent intent = new Intent(this, FavouritesActivityWithAnimation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.do_not_move, android.R.anim.fade_out);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void transitionAnimation(boolean revealing) {
        int coords[] = new int[2];
        binding.addCloseButton.getLocationOnScreen(coords);

        Animator concealAnimation = revealAnimationBuilder
                .setRevealing(revealing)
                .setCoordsOnScreen(coords[0], coords[1])
                .buildAnimation();

        if (concealAnimation != null) {
            if (!revealing) {
                binding.setClickListener(null);
            }

            concealAnimation.start();
        }
    }
}
