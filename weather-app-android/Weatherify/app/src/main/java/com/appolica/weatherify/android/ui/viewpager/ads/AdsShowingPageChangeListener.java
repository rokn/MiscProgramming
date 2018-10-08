package com.appolica.weatherify.android.ui.viewpager.ads;

import android.support.v4.view.ViewPager;

/**
 * Created by Alexander Iliev on 01.12.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
public class AdsShowingPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
    private static final int SWIPES_TO_AD = 5;

    private int swipesCount;
    private OnShouldShowAd callback;

    public AdsShowingPageChangeListener(OnShouldShowAd callback) {
        this.callback = callback;
        swipesCount = 0;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_SETTLING) {
            swipesCount++;

            if (swipesCount >= SWIPES_TO_AD) {
                callback.showAd();
                swipesCount = 0;
            }
        }
    }

    public interface OnShouldShowAd {
        void showAd();
    }
}
