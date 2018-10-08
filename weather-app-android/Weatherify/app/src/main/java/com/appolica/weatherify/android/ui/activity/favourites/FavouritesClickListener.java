package com.appolica.weatherify.android.ui.activity.favourites;

import android.view.View;

/**
 * Created by Bogomil Kolarov on 27.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public interface FavouritesClickListener {
    void onCloseClick();

    void onSettingsClick(View view);

    void onTextFieldClick();
}
