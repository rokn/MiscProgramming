package com.appolica.weatherify.android.ui.activity.settings;

import android.content.Context;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.preferences.units.TemperatureUnit;

import java.util.Arrays;

/**
 * Created by Bogomil Kolarov on 03.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public class TemperatureSwitchAdapterListener
        extends BaseToggleSwitchAdapterListener<TemperatureUnit>
        implements BaseToggleSwitchAdapterListener.OnAdaptedToggleSwitchListener<TemperatureUnit> {

    private Context context;
    private OnTemperatureSelectedListener switchListener;

    public TemperatureSwitchAdapterListener(Context context) {
        super(Arrays.asList(TemperatureUnit.values()));
        this.context = context;

        setToggleListener(this);
    }

    public TemperatureSwitchAdapterListener(Context context,
                                            OnTemperatureSelectedListener switchListener) {
        this(context);

        this.switchListener = switchListener;
    }

    @Override
    public String getLabelForToggleType(TemperatureUnit temperatureUnit, int position) {

        final String unitString = context.getString(temperatureUnit.getUnitResourceId());

        return context.getString(R.string.settingsUnitSwitchTemperature, unitString);
    }

    @Override
    public void onToggleSelected(TemperatureUnit temperatureUnit) {
        if (switchListener != null) {
            switchListener.onTemperatureUnitSelected(temperatureUnit);
        }
    }

    @Override
    public void onToggleDeselected(TemperatureUnit temperatureUnit) {
        // do nothing
    }

    public void setSwitchListener(OnTemperatureSelectedListener switchListener) {
        this.switchListener = switchListener;
    }

    public interface OnTemperatureSelectedListener {
        void onTemperatureUnitSelected(TemperatureUnit temperatureUnit);
    }
}
