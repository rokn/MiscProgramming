package com.appolica.weatherify.android.ui.view.scrollview;

import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bogomil Kolarov on 03.12.16.
 * Copyright © 2016 Appolica. All rights reserved.
 */

public class ScrollViewShowDetector implements ViewTreeObserver.OnScrollChangedListener {

    private static final String TAG = "ScrollViewShowDetector";

    private Rect parentRect = new Rect();

    private List<ObservedView> viewObservables = new ArrayList<>();
    private List<ObservedGroup> groupObservables = new ArrayList<>();

    private ViewObservableListener viewObservableListener = new ViewObservableListener();
    private GroupObservableListener groupObservableListener = new GroupObservableListener();

    private ScrollViewShowListener viewShowListener;
    private ScrollGroupShowListener groupShowListener;

    public ScrollViewShowDetector() {

    }

    @Override
    public void onScrollChanged() {
        if (parentRect != null) {
            notifyChanges();
        }
    }

    public void notifyChanges() {
        if (viewShowListener != null) {
            notifyObservables(viewObservables, viewObservableListener);
        }

        if (groupShowListener != null) {
            notifyObservables(groupObservables, groupObservableListener);
        }
    }

    private <T extends Observable> void notifyObservables(List<T> observables, ObservableScrolledListener<T> observableScrolledListener) {
        for (Observable observable : observables) {
            observable.updateRect();

            final Rect observableRect = observable.getRect();

            if (parentRect.contains(observableRect)) {
                if (!observable.isVisible()) {
                    observableScrolledListener.onObservableScrolledIn((T) observable);
                    observable.setVisible(true);
                }
            } else if (!Rect.intersects(parentRect, observableRect)) {
                if (observable.isVisible()) {
                    observableScrolledListener.onObservableScrolledOut((T) observable);
                    observable.setVisible(false);
                }
            }
        }
    }

    public void updateParentRect(ScrollView scrollView) {
        final int[] screenLocation = new int[2];
        scrollView.getLocationOnScreen(screenLocation);

        getParentRect().set(screenLocation[0],
                screenLocation[1],
                screenLocation[0] + scrollView.getWidth(),
                screenLocation[1] + scrollView.getHeight());

        notifyChanges();
    }

    private class ViewObservableListener implements ObservableScrolledListener<ObservedView> {

        @Override
        public void onObservableScrolledIn(ObservedView observable) {
            viewShowListener.onViewScrolledIn(observable);
        }

        @Override
        public void onObservableScrolledOut(ObservedView observable) {
            viewShowListener.onViewScrolledOut(observable);
        }

    }

    private class GroupObservableListener implements ObservableScrolledListener<ObservedGroup> {

        @Override
        public void onObservableScrolledIn(ObservedGroup group) {
            groupShowListener.onGroupScrolledIn(group);
        }

        @Override
        public void onObservableScrolledOut(ObservedGroup group) {
            groupShowListener.onGroupScrolledOut(group);
        }

    }

    public ObservedView addView(final View view) {
        final ObservedView observedView = new ObservedView(view);
        viewObservables.add(observedView);
        return observedView;
    }

    public ObservedGroup addGroup(View... views) {
        final ObservedGroup observedGroup = new ObservedGroup(views);
        groupObservables.add(observedGroup);
        return observedGroup;
    }

    public void setViewShowListener(ScrollViewShowListener viewShowListener) {
        this.viewShowListener = viewShowListener;
    }

    public void setGroupShowListener(ScrollGroupShowListener groupShowListener) {
        this.groupShowListener = groupShowListener;
    }

    public void setParentRect(Rect parentRect) {
        this.parentRect.set(parentRect);
    }

    protected Rect getParentRect() {
        return parentRect;
    }

    private interface ObservableScrolledListener<T extends Observable> {
        void onObservableScrolledIn(T observable);

        void onObservableScrolledOut(T observable);
    }

    private interface Observable {

        void updateRect();

        Rect getRect();

        void setVisible(boolean visible);

        boolean isVisible();

    }

    public class ObservedView implements Observable {

        private View view;

        private Rect viewRect = new Rect();
        private boolean visible = false;

        public ObservedView(View view) {
            this.view = view;
            updateRect();
        }

        @Override
        public void updateRect() {
            int[] location = new int[2];
            view.getLocationOnScreen(location);

            viewRect.set(location[0],
                    location[1],
                    location[0] + view.getWidth(),
                    location[1] + view.getHeight());
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }

        @Override
        public boolean isVisible() {
            return visible;
        }

        @Override
        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        @Override
        public Rect getRect() {
            return viewRect;
        }
    }

    public class ObservedGroup implements Observable {

        private final SparseArray<ObservedView> observedViews;
        private Rect groupRect = new Rect();

        private boolean visible = false;

        public ObservedGroup(View... views) {
            observedViews = new SparseArray<>();
            Stream.of(views).forEach(view -> observedViews.put(view.getId(), new ObservedView(view)));
        }

        @Override
        public void updateRect() {
            final ObservedView firstObservable = observedViews.valueAt(0);
            firstObservable.updateRect();

            groupRect.set(firstObservable.getRect());

            for (int i = 1; i < observedViews.size(); i++) {
                final ObservedView observedView = observedViews.valueAt(i);

                observedView.updateRect();

                final Rect viewRect = observedView.getRect();

                groupRect.left = Math.min(groupRect.left, viewRect.left);
                groupRect.top = Math.min(groupRect.top, viewRect.top);
                groupRect.right = Math.max(groupRect.right, viewRect.right);
                groupRect.bottom = Math.max(groupRect.bottom, viewRect.bottom);
            }
        }

        @Override
        public Rect getRect() {
            return groupRect;
        }

        @Override
        public void setVisible(boolean visible) {
            this.visible = visible;

            for (int i = 0; i < observedViews.size(); i++) {
                observedViews.valueAt(i).setVisible(visible);
            }
        }

        @Override
        public boolean isVisible() {
            return visible;
        }

        public View getViewById(int id) {
            return observedViews.get(id).getView();
        }
    }

    public interface ScrollGroupShowListener {
        void onGroupScrolledIn(ObservedGroup group);

        void onGroupScrolledOut(ObservedGroup group);
    }

    public interface ScrollViewShowListener {
        void onViewScrolledIn(ObservedView view);

        void onViewScrolledOut(ObservedView view);
    }
}
