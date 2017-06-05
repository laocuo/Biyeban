package com.laocuo.biyeban.main;

import android.support.v4.app.FragmentManager;

import com.laocuo.biyeban.base.ViewPagerAdapter;

import javax.inject.Inject;

/**
 * Created by hoperun on 6/5/17.
 */

public class MainViewPagerAdapter extends ViewPagerAdapter {
    private FragmentManager mFragmentManager;

    @Inject
    MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
    }
}
