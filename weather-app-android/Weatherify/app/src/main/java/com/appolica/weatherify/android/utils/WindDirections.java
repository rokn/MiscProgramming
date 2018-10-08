package com.appolica.weatherify.android.utils;

import com.appolica.weatherify.android.R;

/**
 * Created by aleksandar on 30.08.16.
 */
public enum WindDirections {
    WIND_N(R.string.north),
    WIND_NE(R.string.north_east),
    WIND_E(R.string.east),
    WIND_SE(R.string.south_east),
    WIND_S(R.string.south),
    WIND_SW(R.string.south_west),
    WIND_W(R.string.west),
    WIND_NW(R.string.north_west);

    private int stringResourceId;

    WindDirections(int stringResourceId) {
        this.stringResourceId = stringResourceId;
    }

    public int getStringResourceId() {
        return stringResourceId;
    }
}
