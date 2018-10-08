package com.appolica.weatherify.android.ui.activity.favourites.recyclerview;

import android.databinding.ViewDataBinding;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.annimon.stream.Stream;
import com.appolica.weatherify.android.R;
import com.appolica.weatherify.android.databinding.FavouritesItemBinding;
import com.appolica.weatherify.android.ui.recyclerview.BaseRecyclerViewAdapter;
import com.appolica.weatherify.android.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bogomil Kolarov on 09.06.17.
 * Copyright © 2017 Appolica. All rights reserved.
 */

public class FavouriteTouchHelper extends ItemTouchHelper.Callback {
    private static final String TAG = "FavouriteTouchHelper";

    private List<ItemMovedCallback<FavouritesItemBinding>> callbacks = new ArrayList<>();

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final BaseRecyclerViewAdapter.ItemBindingHolder<FavouritesItemBinding> holder =
                (BaseRecyclerViewAdapter.ItemBindingHolder<FavouritesItemBinding>) viewHolder;

        if (holder.getItemViewType() == FavouritesRVAdapter.TYPE_FAVOURITE_LOCATION) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            return makeMovementFlags(dragFlags, 0);
        } else {

            return 0;
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        final BaseRecyclerViewAdapter.ItemBindingHolder<FavouritesItemBinding> targetHolder =
                (BaseRecyclerViewAdapter.ItemBindingHolder<FavouritesItemBinding>) target;

        final boolean moved = targetHolder.getItemViewType() != FavouritesRVAdapter.TYPE_CURRENT_LOCATION;

        if (moved) {
            Stream.of(callbacks)
                    .forEach(callback ->
                            callback.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition()));
        }

        return moved;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // not supported here
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {

            final BaseRecyclerViewAdapter.ItemBindingHolder<FavouritesItemBinding> holder =
                    (BaseRecyclerViewAdapter.ItemBindingHolder<FavouritesItemBinding>) viewHolder;
            final FavouritesItemBinding binding = holder.getBinding();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final float elevation = Utils.convertDpToPixels(binding.getRoot().getContext(), 10);
                binding.swipeableElement.setElevation(elevation);
            } else {
                binding.getRoot().setBackgroundResource(R.drawable.favourites_item_background_9patch);
            }
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        final BaseRecyclerViewAdapter.ItemBindingHolder<FavouritesItemBinding> holder =
                (BaseRecyclerViewAdapter.ItemBindingHolder<FavouritesItemBinding>) viewHolder;

        final FavouritesItemBinding binding = holder.getBinding();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.swipeableElement.setElevation(0);
        } else {
            binding.getRoot().setBackground(null);
        }

        Stream.of(callbacks).forEach(callback -> callback.onClearView(recyclerView, holder));

    }

    public void addItemMovedCallback(ItemMovedCallback<FavouritesItemBinding> callback) {
        callbacks.add(callback);
    }

    public interface ItemMovedCallback<T extends ViewDataBinding> {
        void onItemMoved(int oldPos, int newPos);
        void onClearView(RecyclerView recyclerView, BaseRecyclerViewAdapter.ItemBindingHolder<T> holder);
    }
}
