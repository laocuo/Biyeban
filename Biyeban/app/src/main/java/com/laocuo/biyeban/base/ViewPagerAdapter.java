/*
 *
 *  * Copyright (C) 2017 laocuo <laocuo@163.com>
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.laocuo.biyeban.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    List<String> mTitles;
    List<Fragment> fragments;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        mTitles = new ArrayList<String>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public void setItems(List<Fragment> fragments, List<String> mTitles) {
        this.fragments = fragments;
        this.mTitles = mTitles;
        notifyDataSetChanged();
    }

    public void setItems(List<Fragment> fragments, String[] mTitles) {
        this.fragments = fragments;
        this.mTitles = Arrays.asList(mTitles);
        notifyDataSetChanged();
    }

    public void addItem(Fragment fragment, String title) {
        fragments.add(fragment);
        mTitles.add(title);
        notifyDataSetChanged();
    }

    public void delItem(int position) {
        mTitles.remove(position);
        fragments.remove(position);
        notifyDataSetChanged();
    }

    public int delItem(String title) {
        int index = mTitles.indexOf(title);
        if (index != -1) {
            delItem(index);
        }
        return index;
    }

    public void swapItems(int fromPos, int toPos) {
        Collections.swap(mTitles, fromPos, toPos);
        Collections.swap(fragments, fromPos, toPos);
        notifyDataSetChanged();
    }

    public void modifyTitle(int position, String title) {
        mTitles.set(position, title);
        notifyDataSetChanged();
    }
}
