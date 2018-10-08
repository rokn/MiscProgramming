package com.appolica.weatherify.android.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.databinding.LayoutAddLocationResultItemBinding;
import com.appolica.weatherify.android.model.AutoSuggestedLocation;

/**
 * Created by aleksandar
 */

class AddLocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private LayoutAddLocationResultItemBinding binding;
    private AddLocationAdapter.SuggestedLocationClick clickCallback;

    private AddLocationViewHolder(LayoutAddLocationResultItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    static AddLocationViewHolder create(LayoutInflater inflater, ViewGroup parent) {
        LayoutAddLocationResultItemBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.layout_add_location_result_item, parent, false);
        return new AddLocationViewHolder(binding);
    }

    void bindTo(AutoSuggestedLocation suggestedLocation, AddLocationAdapter.SuggestedLocationClick clickCallback) {
        this.clickCallback = clickCallback;
        binding.setLocation(suggestedLocation);
        binding.setClickListener(this);
        binding.executePendingBindings();
    }

    @Override
    public void onClick(View v) {
        clickCallback.locationClick(getLayoutPosition());
    }
}
