package com.laocuo.biyeban.main;

import android.support.v4.app.FragmentManager;

import com.laocuo.biyeban.base.IBaseView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by hoperun on 6/5/17.
 */
@Module
public class MainModule {
    private MainActivity mMainActivity;
    public MainModule(MainActivity activity) {
        mMainActivity = activity;
    }

    @Provides
    public IBaseView provideIBaseView() {
        return (IBaseView)mMainActivity;
    }

    @Provides
    public FragmentManager provideFragmentManager() {
        return mMainActivity.getSupportFragmentManager();
    }
}
