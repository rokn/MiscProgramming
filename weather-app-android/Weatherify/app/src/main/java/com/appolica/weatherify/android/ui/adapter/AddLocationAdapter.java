package com.appolica.weatherify.android.ui.adapter;

import android.databinding.ObservableBoolean;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appolica.weatherify.android.WeatherifyApplication;
import com.appolica.weatherify.android.model.AutoSuggestedLocation;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandar
 */

public class AddLocationAdapter extends RecyclerView.Adapter<AddLocationViewHolder> {
    private ObservableBoolean isListEmpty;
    private List<AutoSuggestedLocation> autoSuggestedLocations;
    private SuggestedLocationClick clickCallback;

    public AddLocationAdapter(final SuggestedLocationClick clickCallback) {
        autoSuggestedLocations = new ArrayList<>();
        isListEmpty = new ObservableBoolean(true);
        this.clickCallback = clickCallback;
    }

    public ObservableBoolean getIsListEmpty() {
        return isListEmpty;
    }

    public void setIsListEmpty(ObservableBoolean isListEmpty) {
        this.isListEmpty = isListEmpty;
    }

    @Override
    public AddLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return AddLocationViewHolder.create(LayoutInflater.from(WeatherifyApplication.getInstance()), parent);
    }

    @Override
    public void onBindViewHolder(AddLocationViewHolder holder, int position) {
        holder.bindTo(autoSuggestedLocations.get(position), clickCallback);
    }

    @Override
    public int getItemCount() {
        return autoSuggestedLocations.size();
    }

    public void insertAutosuggestionLocations(AutocompletePredictionBuffer autocompletePredictions) {
        autoSuggestedLocations.clear();
        for (int i = 0; i < autocompletePredictions.getCount(); i++) {
            AutocompletePrediction autocompletePrediction = autocompletePredictions.get(i);
            autoSuggestedLocations.add(
                    new AutoSuggestedLocation(autocompletePrediction.getFullText(null).toString(),
                            autocompletePrediction.getPlaceId()));
        }
        updateObservableBoolean();
        notifyDataSetChanged();
    }

    public void clearAutosuggestionLocations() {
        autoSuggestedLocations.clear();
        updateObservableBoolean();
        notifyDataSetChanged();
    }

    public AutoSuggestedLocation getItem(int index) {
        return autoSuggestedLocations.get(index);
    }

    private void updateObservableBoolean() {
        isListEmpty.set(autoSuggestedLocations.size() == 0);
    }

    public interface SuggestedLocationClick {
        void locationClick(int position);
    }
}
