package com.appolica.weatherify.android.ui.fragment;

import android.support.v7.widget.RecyclerView;
import android.widget.BaseAdapter;

/**
 * Created by Alexander Iliev on 09.06.17.
 * Copyright © 2017 Appolica. All rights reserved.
 */
public interface DetailedForecastContract {
    interface ViewModel {

        void onCreate(String locationName, boolean currentLocation);

        void onDestroy();

        void updateData();

        String getLocationName();

        boolean getShowCurrentLocationIcon();

        int getCurrentIconResource();

        String getCurrentSummary();

        String getTemperature();

        String getApparentTemperature();

        String getCurrentMinMax();

        void onFavoritesClick();

        RecyclerView.Adapter getHourlyAdapter();

        RecyclerView.LayoutManager getHourlyLayoutManager();

        BaseAdapter getDailyAdapter();

        String getHumidityValue();

        String getPrecipitationValue();

        String getVisibilityValue();

        String getWindSpeedValue();

        String getPressureValue();

        String getCloudCoverValue();

        TimeArcModel getTimeArcModel();

        String getHourlySummary();
    }

    interface Controller {
        void onShouldOpenFavourites();
    }
}
