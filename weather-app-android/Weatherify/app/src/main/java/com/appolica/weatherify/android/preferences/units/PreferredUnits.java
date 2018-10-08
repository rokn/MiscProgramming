package com.appolica.weatherify.android.preferences.units;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;

/**
 * Created by Bogomil Kolarov on 09.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public class PreferredUnits extends BaseObservable {
    private TemperatureUnit temperatureUnit;
    private SpeedUnit speedUnit;

    public PreferredUnits() {
    }

    public PreferredUnits(TemperatureUnit temperatureUnit, SpeedUnit speedUnit) {
        this.temperatureUnit = temperatureUnit;
        this.speedUnit = speedUnit;
    }

    @Bindable
    public TemperatureUnit getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(TemperatureUnit temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
        notifyPropertyChanged(BR.temperatureUnit);
    }

    @Bindable
    public SpeedUnit getSpeedUnit() {
        return speedUnit;
    }

    public void setSpeedUnit(SpeedUnit speedUnit) {
        this.speedUnit = speedUnit;
        notifyPropertyChanged(BR.speedUnit);
    }

    public void updateFrom(PreferredUnits preferredUnits) {
        this.temperatureUnit = preferredUnits.temperatureUnit;
        this.speedUnit = preferredUnits.speedUnit;
        notifyChange();
    }
}
