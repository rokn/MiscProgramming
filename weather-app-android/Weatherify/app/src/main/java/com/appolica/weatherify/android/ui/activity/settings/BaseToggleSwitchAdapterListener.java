package com.appolica.weatherify.android.ui.activity.settings;

import java.util.ArrayList;
import java.util.List;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;

/**
 * Created by Bogomil Kolarov on 03.11.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public class BaseToggleSwitchAdapterListener<ToggleType>
        extends BaseToggleSwitch.OnToggleSwitchChangeListener {

    private List<ToggleType> toggleTypes;
    private OnAdaptedToggleSwitchListener<ToggleType> toggleListener;

    public BaseToggleSwitchAdapterListener(List<ToggleType> toggleTypes) {
        this.toggleTypes = toggleTypes;
    }

    @Override
    public void onToggleSwitchChangeListener(int position, boolean isChecked) {
        final ToggleType toggledType = toggleTypes.get(position);

        if (isChecked) {

            toggleListener.onToggleSelected(toggledType);

        } else {

            toggleListener.onToggleDeselected(toggledType);

        }
    }

    public void setToggleListener(OnAdaptedToggleSwitchListener<ToggleType> toggleListener) {
        this.toggleListener = toggleListener;
    }

    public int positionOf(ToggleType toggleType) {
        return toggleTypes.indexOf(toggleType);
    }

    public List<String> getLabels() {
        final List<String> labels = new ArrayList<>();

        for (ToggleType toggleType :
                toggleTypes) {

            final String label = getLabelForToggleType(toggleType, toggleTypes.indexOf(toggleType));

            labels.add(label);
        }

        return labels;
    }

    public String getLabelForToggleType(ToggleType toggleType, int position) {
        return toggleType.toString();
    }

    public interface OnAdaptedToggleSwitchListener<ToggleType> {
        void onToggleSelected(ToggleType toggleType);

        void onToggleDeselected(ToggleType toggleType);
    }

}
