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

package com.laocuo.biyeban.publish;

import android.support.v7.widget.Toolbar;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseActivity;
import com.laocuo.biyeban.publish.news.PublishNewsFragment;

import javax.inject.Inject;

import butterknife.BindView;


public class PublishActivity extends BaseActivity<PublishPresenter> implements IPublishInterface{

    @Inject
    PublishNewsFragment mPublishNewsFragment;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish;
    }

    @Override
    protected void doInject() {
        DaggerPublishComponent.builder()
                .publishModule(new PublishModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void doInit() {
        addFragment(R.id.container, mPublishNewsFragment);
        initToolBar(mToolbar, true, R.string.publish);
    }

    @Override
    protected void getData(boolean isRefresh) {

    }

    @Override
    public void exit() {
        finish();
    }
}
