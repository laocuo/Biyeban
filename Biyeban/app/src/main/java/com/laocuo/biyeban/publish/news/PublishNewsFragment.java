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

package com.laocuo.biyeban.publish.news;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.publish.IPublishInterface;


public class PublishNewsFragment extends BaseFragment {
    private IPublishInterface mIPublishInterface;
    private ActionBar mActionBar;

    public static PublishNewsFragment newInstance(IPublishInterface anInterface) {
        PublishNewsFragment fragment = new PublishNewsFragment();
        fragment.setIPublishInterface(anInterface);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_publishnews;
    }

    @Override
    protected void doInject() {

    }

    @Override
    protected void doInit() {
        setHasOptionsMenu(true);
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setCustomView(R.layout.send_button);
    }

    @Override
    protected void getData(boolean isRefresh) {

    }

    public void setIPublishInterface(IPublishInterface i) {
        mIPublishInterface = i;
    }
}
