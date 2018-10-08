package com.appolica.weatherify.android.ui.activity.enablelocations;

/**
 * Created by Alexander Iliev on 28.06.17.
 * Copyright © 2017 Appolica. All rights reserved.
 */
public interface EnableLocationsContract {
    interface ViewModel {

        int getHideSettingsButton();

        void onGoToFavourites();

        void onGoToSettings();
    }

    interface Controller {
        void onGoToFavourites();

        void onGoToSettings();
    }
}
