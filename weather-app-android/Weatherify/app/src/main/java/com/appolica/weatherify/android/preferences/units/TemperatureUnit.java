package com.appolica.weatherify.android.preferences.units;

import com.appolica.weatherify.android.R;

import javax.measure.quantity.Temperature;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

/**
 * Created by Bogomil Kolarov on 03.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
public enum TemperatureUnit {
    FAHRENHEIT(NonSI.FAHRENHEIT, R.string.fahrenheit),
    CELSIUS(SI.CELSIUS, R.string.celsius);

    private final Unit<Temperature> unit;
    private final int unitResourceId;

    TemperatureUnit(Unit<Temperature> unit, int unitResourceId) {
        this.unit = unit;
        this.unitResourceId = unitResourceId;
    }

    public Unit<Temperature> getUnit() {
        return unit;
    }

    public int getUnitResourceId() {
        return unitResourceId;
    }

}
