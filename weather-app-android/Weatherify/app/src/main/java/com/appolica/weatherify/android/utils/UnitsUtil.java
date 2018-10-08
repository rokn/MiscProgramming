package com.appolica.weatherify.android.utils;

import com.appolica.weatherify.android.preferences.units.SpeedUnit;
import com.appolica.weatherify.android.preferences.units.TemperatureUnit;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.SI;

/**
 * Created by Bogomil Kolarov on 08.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public class UnitsUtil {

    public static double metersPerSecondTo(double metersPerSecond, SpeedUnit speedUnit) {

        final UnitConverter converter = SI.METERS_PER_SECOND.getConverterTo(speedUnit.getUnit());

        return converter.convert(metersPerSecond);
    }

    public static double celsiusTo(double temperature, TemperatureUnit temperatureUnit) {

        final UnitConverter converter = SI.CELSIUS.getConverterTo(temperatureUnit.getUnit());

        return converter.convert(temperature);
    }
}
