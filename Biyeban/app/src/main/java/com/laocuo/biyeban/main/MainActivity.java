package com.laocuo.biyeban.main;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseActivity;
import com.laocuo.biyeban.login.BiyebanUser;
import com.laocuo.biyeban.main.chatroom.ChatRoomFragment;
import com.laocuo.biyeban.main.contacts.ContactsFragment;
import com.laocuo.biyeban.main.freshnews.FreshNewsFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import cn.bmob.v3.BmobUser;

public class MainActivity extends BaseActivity<MainPresenter> implements ViewPager.OnPageChangeListener {
    @Inject
    MainViewPagerAdapter mMainViewPagerAdapter;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;

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
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mViewPager.setAdapter(mMainViewPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mNaviList.add(R.id.navigation_freshnews);
        mNaviList.add(R.id.navigation_contacts);
        mNaviList.add(R.id.navigation_chatroom);

        String title1 = getResources().getString(R.string.title_freshnews);
        mTitleList.add(title1);
        mFragmentList.add(FreshNewsFragment.newInstance(title1));
        String title2 = getResources().getString(R.string.title_contacts);
        mTitleList.add(title2);
        mFragmentList.add(ContactsFragment.newInstance(title2));
        String title3 = getResources().getString(R.string.title_chatroom);
        mTitleList.add(title3);
        mFragmentList.add(ChatRoomFragment.newInstance(title3));
    }

    @Override
    protected void doLoadData() {
        mMainViewPagerAdapter.setItems(mFragmentList, mTitleList);
    }

    @Override
    public void updateUI() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final BiyebanUser user = BmobUser.getCurrentUser(BiyebanUser.class);
        if (user != null) {
//            user.logOut();
        }
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
}
