package com.laocuo.biyeban.test;

import com.laocuo.biyeban.BiyebanApp;
import com.laocuo.biyeban.greendao.DaoSession;

import dagger.Module;
import dagger.Provides;

/**
 * Created by hoperun on 5/26/17.
 */

@Module
public class TestModule {
    private TestContact.View mView;
    public TestModule(TestContact.View view) {
        mView = view;
    }

    @Provides
    public TestContact.View provideTestView() {
        return mView;
    }

    @Provides
    public DaoSession provideDaoSession() {
        return BiyebanApp.getInstance().getDaoSession();
    }
}
