package com.appolica.weatherify.android.ui.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;

public class DailyLinearLayout extends LinearLayout {
    private Adapter adapter;
    private boolean adapterAttached;

    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            reload();
        }
    };

    public DailyLinearLayout(Context context) {
        super(context);
    }

    public DailyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DailyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void reload() {
        removeAllViews();
        if (adapter != null) {
            int count = adapter.getCount();
            for (int i = 0; i < count; i++) {
                View view = adapter.getView(i, null, this);
                addView(view);
                requestLayout();
            }
            requestLayout();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (adapter != null && adapterAttached) {
            detachAdapter();
        }
    }

    private void detachAdapter() {
        adapter.unregisterDataSetObserver(dataSetObserver);
        adapterAttached = false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (adapter != null && !adapterAttached) {
            attachAdapter();
        }
    }

    private void attachAdapter() {
        adapter.registerDataSetObserver(dataSetObserver);
        adapterAttached = true;
    }

    public void setAdapter(Adapter adapter) {
        if (this.adapter != null) {
            if (adapterAttached) {
                detachAdapter();
            }
        }
        adapterAttached = false;
        this.adapter = adapter;
        if (adapter != null) {
            attachAdapter();
        }
        reload();
    }
}