package com.laocuo.biyeban.main.freshnews;

import com.laocuo.biyeban.base.IBasePresenter;
import com.laocuo.biyeban.base.IBaseView;

import javax.inject.Inject;

/**
 * Created by hoperun on 6/5/17.
 */

public class FreshNewsPresenter implements IBasePresenter {
    private IBaseView mIBaseView;

    @Override
    public void loadData() {

    }

    @Inject
    FreshNewsPresenter(IBaseView view) {
        mIBaseView = view;
    }
}
