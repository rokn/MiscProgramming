package com.appolica.weatherify.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.appolica.weatherify.android.ui.fragment.CanScrollListener;
import com.appolica.weatherify.android.ui.fragment.DetailedForecastFragment;

import java.util.List;

/**
 * Created by Alexander Iliev
 */

public class MainPagerAdapter extends FragmentAwarePagerAdapter {

    private static final String TAG = "MainPagerAdapter";
    private List<String> forecastLocationIds;
    private boolean hasCurrentLocation;

    private CanScrollListener canScrollListener;

    private SparseArray<Fragment> fragments = new SparseArray<>();

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setCanScrollListener(CanScrollListener canScrollListener) {
        this.canScrollListener = canScrollListener;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem() called with: position = [" + position + "]");
        return DetailedForecastFragment.newInstance(
                forecastLocationIds.get(position),
                (hasCurrentLocation && position == 0));
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        fragments.remove(position);
    }

    @Override
    public int getCount() {
        return forecastLocationIds.size();
    }

    public void setForecastLocationIds(List<String> forecastLocationIds, boolean hasCurrentLocation) {
        this.forecastLocationIds = forecastLocationIds;
        this.hasCurrentLocation = hasCurrentLocation;
        notifyDataSetChanged();
    }

    public void addForecastLocationId(String newForecastLocationId, boolean isCurrentLocation) {
        this.forecastLocationIds.add(newForecastLocationId);
        this.hasCurrentLocation |= isCurrentLocation;
        notifyDataSetChanged();
    }

    public List<String> getForecastLocationIds() {
        return forecastLocationIds;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragments.put(position, fragment);
        ((DetailedForecastFragment) fragment).setCanScrollListener(canScrollListener);

        return fragment;
    }
}
