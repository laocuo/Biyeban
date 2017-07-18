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

package com.laocuo.biyeban.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseActivity;
import com.laocuo.biyeban.base.ExitApplication;
import com.laocuo.biyeban.base.ViewPagerAdapter;
import com.laocuo.biyeban.main.chatroom.ChatRoomFragment;
import com.laocuo.biyeban.main.contacts.ContactsFragment;
import com.laocuo.biyeban.main.freshnews.FreshNewsFragment;
import com.laocuo.biyeban.settings.SettingsActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cn.bmob.v3.update.BmobUpdateAgent;

public class MainActivity extends BaseActivity<MainPresenter> implements ViewPager.OnPageChangeListener {
    @Inject
    ViewPagerAdapter mViewPagerAdapter;

    @Inject
    FreshNewsFragment mFreshNewsFragment;
    @Inject
    ContactsFragment mContactsFragment;
    @Inject
    ChatRoomFragment mChatRoomFragment;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitApplication.getInstance().addActivity(this);
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.expand_vertical_entry, R.anim.hold);
    }

    private long mExitTime = 0;
    private List<Integer> mNaviList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitleList = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_freshnews:
                    mViewPager.setCurrentItem(0, true);
                    return true;
                case R.id.navigation_contacts:
                    mViewPager.setCurrentItem(1, true);
                    return true;
                case R.id.navigation_chatroom:
                    mViewPager.setCurrentItem(2, true);
                    return true;
            }
            return false;
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            SettingsActivity.launch(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void doInject() {
        DaggerMainComponent.builder()
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void doInit() {
        initToolBar(mToolbar, false, R.string.app_name);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mNaviList.add(R.id.navigation_freshnews);
        mNaviList.add(R.id.navigation_contacts);
        mNaviList.add(R.id.navigation_chatroom);

        mTitleList.add(getResources().getString(R.string.title_freshnews));
        mFragmentList.add(mFreshNewsFragment);
        mTitleList.add(getResources().getString(R.string.title_contacts));
        mFragmentList.add(mContactsFragment);
        mTitleList.add(getResources().getString(R.string.title_chatroom));
        mFragmentList.add(mChatRoomFragment);

        //        BmobUpdateAgent.initAppVersion();
        BmobUpdateAgent.setUpdateOnlyWifi(true);
        BmobUpdateAgent.setUpdateCheckConfig(false);
        BmobUpdateAgent.update(this);
    }

    @Override
    protected void getData(boolean isRefresh) {
        mViewPagerAdapter.setItems(mFragmentList, mTitleList);
    }

    @Override
    public void onBackPressed() {
        _exit();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        navigation.setSelectedItemId(mNaviList.get(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 退出
     */
    private void _exit() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.expand_vertical_exit);
    }
}
