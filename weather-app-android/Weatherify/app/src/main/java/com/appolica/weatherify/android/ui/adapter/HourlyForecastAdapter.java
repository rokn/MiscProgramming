package com.appolica.weatherify.android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.appolica.weatherify.android.WeatherifyApplication;
import com.appolica.weatherify.android.model.ForecastDataBlock;
import com.appolica.weatherify.android.preferences.units.PreferredUnits;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandar on 26.08.16.
 */
public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyItemViewHolder> {
    private List<HourForecastModel> hourlyForecasts;
    private PreferredUnits preferredUnits;

    public HourlyForecastAdapter(PreferredUnits preferredUnits) {

        this.preferredUnits = preferredUnits;
        hourlyForecasts = new ArrayList<>();
    }

    @Override
    public HourlyItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return HourlyItemViewHolder.create(LayoutInflater.from(WeatherifyApplication.getInstance()), parent);
    }

    @Override
    public void onBindViewHolder(HourlyItemViewHolder holder, int position) {
        holder.bindTo(hourlyForecasts.get(position), preferredUnits);
    }

    @Override
    public int getItemCount() {
        return hourlyForecasts.size();
    }

    public void insertHourlyForecastData(String timezone, ForecastDataBlock hourlyForecast) {
        hourlyForecasts.clear();
        final List<HourForecastModel> models = Stream.of(hourlyForecast.getData())
                .map(forecast -> new HourForecastModel(forecast, timezone))
                .collect(Collectors.toList());

        hourlyForecasts.addAll(models);

//        hourlyForecasts.addAll(hourlyForecast.getData());
        notifyDataSetChanged();
    }
}
