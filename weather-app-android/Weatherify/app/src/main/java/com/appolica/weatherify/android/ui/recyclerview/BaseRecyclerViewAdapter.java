package com.appolica.weatherify.android.ui.recyclerview;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Bogomil Kolarov on 04.10.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public abstract class BaseRecyclerViewAdapter<DataType, Binding extends ViewDataBinding>
        extends RecyclerView.Adapter<BaseRecyclerViewAdapter.ItemBindingHolder<Binding>> {

    private static final String TAG = "BaseRecyclerViewAdapter";

    private List<DataType> data = new ArrayList<>();

    public static class ItemBindingHolder<ItemBinding extends ViewDataBinding>
            extends RecyclerView.ViewHolder {

        private ItemBinding binding;

        public ItemBindingHolder(ItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ItemBinding getBinding() {
            return binding;
        }
    }

    @Override
    public ItemBindingHolder<Binding> onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        final Binding binding =
                DataBindingUtil.inflate(inflater, getLayoutId(viewType), parent, false);

        final ItemBindingHolder<Binding> viewHolder = new ItemBindingHolder<>(binding);

        onViewInflated(binding);

        return viewHolder;
    }

    public void onViewInflated(final Binding binding) {

    }

    protected abstract int getLayoutId(int viewType);

    @Override
    public void onBindViewHolder(ItemBindingHolder<Binding> holder, int position) {
        final Binding binding = holder.getBinding();

        onBindViewBinding(binding, position);
    }

    public void onBindViewBinding(Binding binding, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addData(List<DataType> data) {
        final int oldSize = this.data.size();

        final Iterator<DataType> iterator = data.iterator();

        while (iterator.hasNext()) {
            final DataType item = iterator.next();

            if (this.data.contains(item)) {
                iterator.remove();
            }
        }

        this.data.addAll(data);

        if (this.data.size() != oldSize) {
            notifyDataSetChanged();
        }
    }

    public void addItem(DataType element) {
        final int oldSize = this.data.size();

        data.add(element);

        if (this.data.size() != oldSize) {
            notifyDataSetChanged();
        }
    }

    public void addItemTop(DataType element) {
        final int oldSize = this.data.size();

        data.add(0, element);

        if (this.data.size() != oldSize) {
            notifyElementInserted(0);
        }
    }

    public void addItemTop(DataType element, boolean notifyInserted) {
        if (notifyInserted) {
            addItemTop(element);
        } else {
            final int oldSize = this.data.size();

            data.add(0, element);

            if (this.data.size() != oldSize) {
                notifyDataSetChanged();
            }
        }
    }

    public void updateItemAt(int position, DataType updatedElement) {
        data.set(position, updatedElement);
        notifyItemChanged(position);
    }

    public void removeItem(DataType element) {
        final int removedObjectIndex = data.indexOf(element);
        data.remove(element);
        notifyElementRemoved(removedObjectIndex);
    }

    public void removeItem(DataType element, boolean notifyRemoved) {
        if (notifyRemoved) {
            removeItem(element);
        } else {
            data.remove(element);
            notifyDataSetChanged();
        }
    }

    public void removeAt(int position) {
        data.remove(position);
        notifyElementRemoved(position);
    }

    public void removeAt(int position, boolean notifyRemoved) {
        if (notifyRemoved) {
            removeAt(position);
        } else {
            data.remove(position);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void setData(@NonNull List<DataType> data) {
        if (data == null) {
            throw new IllegalArgumentException("Data must not be null.");
        }
        this.data = data;
        notifyDataSetChanged();
    }

    public List<DataType> getData() {
        return data;
    }

    public DataType getItem(int position) {
        return data.get(position);
    }

    public void notifyElementRemoved(int position) {
        notifyItemRemoved(position);
    }

    public void notifyElementInserted(int position) {
        notifyItemInserted(position);
    }

}

