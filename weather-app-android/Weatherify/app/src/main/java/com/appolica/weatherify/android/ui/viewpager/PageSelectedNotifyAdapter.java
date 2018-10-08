package com.appolica.weatherify.android.ui.viewpager;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;


public class PageSelectedNotifyAdapter extends ViewPager.SimpleOnPageChangeListener {

    private FragmentProvider fragmentProvider;
    private int childCount = 0;

    public PageSelectedNotifyAdapter(FragmentProvider fragmentProvider) {
        this.fragmentProvider = fragmentProvider;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset == 0f) {
            for (int i = 0; i < childCount; i++) {
                final Fragment fragment = fragmentProvider.getFragmentAt(i);

                if (fragment != null && fragment instanceof PageSelectedNotifyAdapter.OnFragmentSelectedListener) {
                    if (i == position) {
                        ((PageSelectedNotifyAdapter.OnFragmentSelectedListener) fragment).onFragmentSelected();
                    } else {
                        ((PageSelectedNotifyAdapter.OnFragmentSelectedListener) fragment).onFragmentDeselected();
                    }
                }
            }
        }
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public interface OnFragmentSelectedListener {
        void onFragmentSelected();

        void onFragmentDeselected();
    }

    public interface FragmentProvider {
        @Nullable
        Fragment getFragmentAt(int position);
    }
}
