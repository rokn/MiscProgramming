package com.appolica.weatherify.android.preferences.units;

import java.util.Locale;

/**
 * Created by Bogomil Kolarov on 04.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public class LocalizedUnits {

    public static final String COUNTRY_CODE_US = "US";
    public static final String COUNTRY_CODE_LIBERIA = "LR";
    public static final String COUNTRY_CODE_BURMA = "MM";
    public static final String COUNTRY_CODE_BAHAMAS = "BHS";
    public static final String COUNTRY_CODE_BELIZE = "BLZ";
    public static final String COUNTRY_CODE_CAYMAN_ISLANDS = "CYM";
    public static final String COUNTRY_CODE_REPUBLIC_OF_PALAU = "PLW";

    public static SpeedUnit getDefaultWindUnit() {
        return getWindUnit(Locale.getDefault());
    }

    public static SpeedUnit getWindUnit(Locale locale) {

        final String countryCode = locale.getCountry();

        if (countryCode.equals(COUNTRY_CODE_US)
                || countryCode.equals(COUNTRY_CODE_LIBERIA)
                || countryCode.equals(COUNTRY_CODE_BURMA)) {

            return SpeedUnit.MPH;
        } else {

            return SpeedUnit.MS;
        }
    }

    public static TemperatureUnit getDefaultTemperatureUnit() {
        return getTemperatureUnit(Locale.getDefault());
    }

    public static TemperatureUnit getTemperatureUnit(Locale locale) {

        final String countryCode = locale.getCountry();

        if (countryCode.equals(COUNTRY_CODE_BAHAMAS)
                || countryCode.equals(COUNTRY_CODE_BELIZE)
                || countryCode.equals(COUNTRY_CODE_CAYMAN_ISLANDS)
                || countryCode.equals(COUNTRY_CODE_REPUBLIC_OF_PALAU)
                || countryCode.equals(COUNTRY_CODE_US)) {

            return TemperatureUnit.FAHRENHEIT;
        } else {

            return TemperatureUnit.CELSIUS;
        }
    }

}
