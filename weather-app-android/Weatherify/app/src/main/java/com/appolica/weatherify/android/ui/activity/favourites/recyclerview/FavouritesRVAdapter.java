package com.appolica.weatherify.android.ui.activity.favourites.recyclerview;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.databinding.FavouritesItemBinding;
import com.appolica.weatherify.android.model.ForecastLocation;
import com.appolica.weatherify.android.preferences.units.PreferredUnits;
import com.appolica.weatherify.android.ui.activity.favourites.FavouritesActivity;
import com.appolica.weatherify.android.ui.recyclerview.BaseRecyclerViewAdapter;
import com.appolica.weatherify.android.ui.view.swiperemovervlayout.SimpleSwipeListener;
import com.appolica.weatherify.android.ui.view.swiperemovervlayout.SwipeListener;

import java.util.Collections;
import java.util.List;

/**
 * Created by Bogomil Kolarov on 04.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */
public class FavouritesRVAdapter extends
        BaseRecyclerViewAdapter<ForecastLocation, FavouritesItemBinding>
        implements FavouriteTouchHelper.ItemMovedCallback<FavouritesItemBinding> {

    public static final int TYPE_CURRENT_LOCATION = 524;
    public static final int TYPE_FAVOURITE_LOCATION = 304;
    private static final String TAG = "FavouritesRVAdapter";

    private SwipeListener swipeListener;
    private OnSwipeRemovedItemListener swipeRemovedItemListener;
    private OnReorderedListener onReorderedListener;

    private PreferredUnits preferredUnits;

    private int minReorderedPos = -1;

    public FavouritesRVAdapter(PreferredUnits preferredUnits) {
        this.preferredUnits = preferredUnits;

        swipeListener = SimpleSwipeListener.forSwiped(swipeableView -> {
            final FavouritesItemBinding binding = DataBindingUtil.findBinding(swipeableView);

            if (swipeRemovedItemListener != null) {
                swipeRemovedItemListener.onItemSwiped(binding.getLocation());
            }
        });
    }

    @Override
    public int getItemViewType(int position) {

        final int viewType;
        if (position == 0 && hasCurrentFavouriteLocation()) {

            viewType = TYPE_CURRENT_LOCATION;

        } else {

            viewType = TYPE_FAVOURITE_LOCATION;

        }

        return viewType;
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.favourites_item;
    }

    @Override
    public void onViewInflated(FavouritesItemBinding binding) {
        binding.setSwipeListener(swipeListener);
    }

    @Override
    public void onBindViewBinding(FavouritesItemBinding binding, int position) {
        switch (getItemViewType(position)) {
            case TYPE_CURRENT_LOCATION:

                binding.setIsCurrentLocation(true);

                break;
            case TYPE_FAVOURITE_LOCATION:

                binding.setIsCurrentLocation(false);

                break;
        }

        binding.setPreferredUnits(preferredUnits);
        binding.setLocation(getItem(position));
        binding.executePendingBindings();
    }

    public boolean hasCurrentFavouriteLocation() {
        List<ForecastLocation> data = getData();
        if(data.isEmpty()) {
            return false;
        }

        return data.get(FavouritesActivity.CURRENT_LOCATION_POSITION).isCurrentLocation();
    }

    public void setSwipeRemovedItemListener(OnSwipeRemovedItemListener swipeRemovedItemListener) {
        this.swipeRemovedItemListener = swipeRemovedItemListener;
    }

    @Override
    public void onItemMoved(int oldPos, int newPos) {
        Collections.swap(getData(), oldPos, newPos);
        notifyItemMoved(oldPos, newPos);
        minReorderedPos = Math.min(oldPos, newPos);
    }

    @Override // onReorderedDone
    public void onClearView(RecyclerView recyclerView, ItemBindingHolder<FavouritesItemBinding> holder) {
        if (minReorderedPos > -1) {
            final int offset = getData().get(0).isCurrentLocation() ? 0 : 1;
            Stream.of(getData())
                    .indexed(minReorderedPos, 1)
                    .forEachIndexed((index, forecast) -> forecast.getSecond().setOrderPosition(index + offset));

            if (onReorderedListener != null) {
                onReorderedListener.onReordered(minReorderedPos);
            }

            minReorderedPos = -1;
        }
    }

    public void setOnReorderedListener(OnReorderedListener onReorderedListener) {
        this.onReorderedListener = onReorderedListener;
    }

    public interface OnSwipeRemovedItemListener {
        void onItemSwiped(ForecastLocation location);
    }

    public interface OnReorderedListener {
        void onReordered(int fromPos);
    }
}
