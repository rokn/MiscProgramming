package com.appolica.weatherify.android.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.appolica.weatherify.android.ui.viewpager.PageSelectedNotifyAdapter;


public abstract class FragmentAwarePagerAdapter
        extends FragmentStatePagerAdapter
        implements PageSelectedNotifyAdapter.FragmentProvider {

    private static final String TAG = "FrAwarePagerAdapter";

    private SparseArray<Fragment> fragments = new SparseArray<>();

    public FragmentAwarePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    @Nullable
    public Fragment getFragmentAt(int position) {
        return fragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        fragments.remove(position);
    }
}
