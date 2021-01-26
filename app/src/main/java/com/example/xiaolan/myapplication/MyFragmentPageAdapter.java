package com.example.xiaolan.myapplication;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyFragmentPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> myListFragment;//增加
    Context context;

    public MyFragmentPageAdapter(FragmentManager fm, Context context, List<Fragment> listFragment) {
        super(fm);
        this.context = context;
        this.myListFragment=listFragment;//增加
    }


    @Override
    public Fragment getItem(int i) {
        return myListFragment.get(i);
    }

    @Override
    public int getCount() {
        return myListFragment.size();
    }
}
