package com.laocuo.biyeban.main.freshnews;

import com.laocuo.biyeban.base.IBaseView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by hoperun on 6/5/17.
 */

@Module
public class FreshNewsModule {
    private IBaseView mIBaseView;

    public FreshNewsModule(IBaseView view) {
        mIBaseView = view;
    }

    @Provides
    IBaseView provideIBaseView() {
        return mIBaseView;
    }
}
