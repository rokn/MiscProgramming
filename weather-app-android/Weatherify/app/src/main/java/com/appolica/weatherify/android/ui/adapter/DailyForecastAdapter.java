package com.appolica.weatherify.android.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.appolica.weatherify.android.databinding.LayoutForecastDailyItemBinding;
import com.appolica.weatherify.android.model.ForecastDataBlock;
import com.appolica.weatherify.android.model.ForecastDataPoint;
import com.appolica.weatherify.android.preferences.units.PreferredUnits;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandar
 */
public class DailyForecastAdapter extends BaseAdapter {
    private List<ForecastDataPoint> dailyForecasts;
    private PreferredUnits preferredUnits;

    public DailyForecastAdapter(PreferredUnits preferredUnits) {
        this.preferredUnits = preferredUnits;
        this.dailyForecasts = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return dailyForecasts.size();
    }

    @Override
    public Object getItem(int i) {
        return dailyForecasts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        LayoutForecastDailyItemBinding itemBinding =
                LayoutForecastDailyItemBinding.inflate(inflater, null, false);

        itemBinding.setPreferredUnits(preferredUnits);
        itemBinding.setDailyForecastData(dailyForecasts.get(i));

        final boolean isLastElement = (i == dailyForecasts.size() - 1);

        itemBinding.setLastElement(isLastElement);

        return itemBinding.getRoot();
    }

    public void insertDailyForecastData(ForecastDataBlock dailyForecast) {
        dailyForecasts.clear();
        dailyForecasts.addAll(dailyForecast.getData());
        dailyForecasts.remove(0);
        notifyDataSetChanged();
    }
}
