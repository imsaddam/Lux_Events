package com.imsaddam.luxevents.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<TabPagerItem> mTabs;

    public ViewPagerAdapter(FragmentManager fragmentManager, List<TabPagerItem> tabs) {
        super(fragmentManager);
        this.mTabs = tabs;
    }

    public void setDatasource(List<TabPagerItem> datasource) {
        mTabs = datasource;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int i) {
        return mTabs.get(i).getFragment();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getTitle();
    }
}