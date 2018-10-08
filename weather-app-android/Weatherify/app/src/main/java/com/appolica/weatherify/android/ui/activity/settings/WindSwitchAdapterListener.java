package com.appolica.weatherify.android.ui.activity.settings;

import android.content.Context;

import com.appolica.weatherify.android.preferences.units.SpeedUnit;

import java.util.Arrays;

/**
 * Created by Bogomil Kolarov on 03.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public class WindSwitchAdapterListener
        extends BaseToggleSwitchAdapterListener<SpeedUnit>
        implements BaseToggleSwitchAdapterListener.OnAdaptedToggleSwitchListener<SpeedUnit> {

    private Context context;
    private OnWindSwitchListener switchListener;

    public WindSwitchAdapterListener(Context context) {
        super(Arrays.asList(SpeedUnit.values()));
        this.context = context;

        setToggleListener(this);
    }

    public WindSwitchAdapterListener(Context context, OnWindSwitchListener switchListener) {
        this(context);

        this.switchListener = switchListener;
    }

    @Override
    public String getLabelForToggleType(SpeedUnit speedUnit, int position) {
        return context.getString(speedUnit.getUnitResourceId());
    }

    @Override
    public void onToggleSelected(SpeedUnit windUnit) {
        if (switchListener != null) {
            switchListener.onWindUnitSelected(windUnit);
        }
    }

    @Override
    public void onToggleDeselected(SpeedUnit windUnit) {
        // do nothing
    }


    public void setSwitchListener(OnWindSwitchListener switchListener) {
        this.switchListener = switchListener;
    }

    public interface OnWindSwitchListener {
        void onWindUnitSelected(SpeedUnit windUnit);
    }

}
