package com.laocuo.biyeban.main;

import com.laocuo.biyeban.base.IBasePresenter;
import com.laocuo.biyeban.base.IBaseView;

import javax.inject.Inject;

/**
 * Created by hoperun on 6/5/17.
 */

public class MainPresenter implements IBasePresenter {
    private IBaseView mView;

    @Override
    public void loadData() {

    }

    @Inject
    MainPresenter(IBaseView view) {
        mView = view;
    }
}
