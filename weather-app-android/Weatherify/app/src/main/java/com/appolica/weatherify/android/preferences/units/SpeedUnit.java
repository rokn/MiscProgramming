package com.appolica.weatherify.android.preferences.units;

import com.appolica.weatherify.android.R;

import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

/**
 * Created by Bogomil Kolarov on 03.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
public enum SpeedUnit {
    MPH(NonSI.MILES_PER_HOUR, R.string.miles_per_hour),
    KMH(NonSI.KILOMETERS_PER_HOUR, R.string.kilometers_per_hour),
    MS(SI.METERS_PER_SECOND, R.string.meters_per_second),
    KNOTS(NonSI.KNOT, R.string.knots);

    private final Unit<Velocity> unit;
    private final int unitResourceId;

    SpeedUnit(Unit<Velocity> unit, int unitResourceId) {
        this.unit = unit;
        this.unitResourceId = unitResourceId;
    }

    public Unit<Velocity> getUnit() {
        return unit;
    }

    public int getUnitResourceId() {
        return unitResourceId;
    }

}
