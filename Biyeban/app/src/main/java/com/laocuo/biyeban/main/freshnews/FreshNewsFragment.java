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

package com.laocuo.biyeban.main.freshnews;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.publish.PublishActivity;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.SnackbarUtil;
import com.laocuo.biyeban.utils.Utils;
import com.laocuo.recycler.helper.RecyclerViewHelper;
import com.laocuo.recycler.listener.OnRequestDataListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;


public class FreshNewsFragment extends BaseFragment<FreshNewsPresenter>
        implements IFreshNewsView, OnRequestDataListener {
    private static final String TAG = "FreshNews";
    private static final String TYPE_KEY = "TypeKey";
    private String mTitle;

    @Inject
    FreshNewsListAdapter mAdapter;

    @BindView(R.id.freshnews_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;

    public static FreshNewsFragment newInstance() {
        FreshNewsFragment fragment = new FreshNewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(TYPE_KEY);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_freshnews;
    }

    @Override
    protected void doInject() {
        DaggerFreshNewsComponent.builder()
                .freshNewsModule(new FreshNewsModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void doInit() {
        RecyclerViewHelper.initRecyclerViewV(mContext, mRecyclerView, true, mAdapter, this);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                SnackbarUtil.showShortSnackbar(view, "Replace with your own action");
                startActivity(new Intent(getActivity(), PublishActivity.class));
            }
        });
    }

    @Override
    protected void getData(boolean isRefresh) {
        debug("FreshNewsFragment getData="+isRefresh);
        if (isRefresh == true) {
            mPresenter.swipeRefresh();
        } else {
            mPresenter.loadData();
        }
    }

    private void debug(String log) {
        L.d(TAG+"-"+log);
    }

    @Override
    public void loadData(List<FreshNewsItem> data) {
        mAdapter.updateItems(data);
    }

    @Override
    public void loadMoreData(List<FreshNewsItem> data) {
        mAdapter.addItems(data);
        finishRefresh();
    }

    @Override
    public void loadNoData() {

    }

    @Override
    public void onLoadMore() {
        mPresenter.loadMoreData();
    }
}
