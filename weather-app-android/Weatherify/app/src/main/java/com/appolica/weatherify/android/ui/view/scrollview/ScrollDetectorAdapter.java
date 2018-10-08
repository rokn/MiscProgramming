package com.appolica.weatherify.android.ui.view.scrollview;

import android.graphics.Rect;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class ScrollDetectorAdapter extends ScrollViewShowDetector
        implements ScrollViewShowDetector.ScrollViewShowListener,
        ScrollViewShowDetector.ScrollGroupShowListener {

    private static final String TAG = "ScrollDetectorAdapter";

    private final Map<ObservedView, ViewScrolledInListener> viewScrolledListeners = new HashMap<>();
    private final Map<ObservedGroup, GroupScrolledInListener> groupScrolledListeners = new HashMap<>();

    public ScrollDetectorAdapter() {
        setViewShowListener(this);
        setGroupShowListener(this);
    }

    @Override
    public void onViewScrolledIn(ObservedView observedView) {
        viewScrolledListeners.get(observedView).onViewScrolledIn(observedView.getView(), observedView.getRect());
    }

    @Override
    public void onViewScrolledOut(ObservedView observedView) {
    }

    @Override
    public void onGroupScrolledIn(ScrollViewShowDetector.ObservedGroup group) {
        groupScrolledListeners.get(group).onGroupScrolledIn(group);
    }

    @Override
    public void onGroupScrolledOut(ScrollViewShowDetector.ObservedGroup group) {

    }

    public void addView(ViewScrolledInListener listener, View view) {
        final ObservedView observedView = addView(view);
        viewScrolledListeners.put(observedView, listener);
    }

    public void addGroup(GroupScrolledInListener listener, View... views) {
        final ObservedGroup observedGroup = addGroup(views);
        groupScrolledListeners.put(observedGroup, listener);
    }

    public interface ViewScrolledInListener {
        void onViewScrolledIn(View view, Rect viewRect);
    }

    public interface GroupScrolledInListener {
        void onGroupScrolledIn(ObservedGroup group);
    }
}
