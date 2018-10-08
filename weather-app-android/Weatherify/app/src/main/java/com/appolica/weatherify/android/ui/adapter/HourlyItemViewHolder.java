package com.appolica.weatherify.android.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.databinding.LayoutForecastHourlyItemBinding;
import com.appolica.weatherify.android.preferences.units.PreferredUnits;

/**
 * Created by aleksandar
 */
class HourlyItemViewHolder extends RecyclerView.ViewHolder {
    private LayoutForecastHourlyItemBinding binding;

    private HourlyItemViewHolder(LayoutForecastHourlyItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    static HourlyItemViewHolder create(LayoutInflater inflater, ViewGroup parent) {
        LayoutForecastHourlyItemBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.layout_forecast_hourly_item, parent, false);

        return new HourlyItemViewHolder(binding);
    }

    void bindTo(HourForecastModel forecastModel, PreferredUnits preferredUnits) {
        binding.setPreferredUnits(preferredUnits);
        binding.setHourlyForecastModel(forecastModel);
        binding.executePendingBindings();
    }
}
