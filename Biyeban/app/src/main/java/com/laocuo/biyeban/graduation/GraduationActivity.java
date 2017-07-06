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

package com.laocuo.biyeban.graduation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseActivity;
import com.laocuo.biyeban.graduation.create.GraduCreateFragment;
import com.laocuo.biyeban.graduation.main.GraduMainFragment;
import com.laocuo.biyeban.graduation.join.GraduJoinFragment;
import com.laocuo.biyeban.utils.Utils;

import javax.inject.Inject;

import butterknife.BindView;

public class GraduationActivity extends BaseActivity<GraduationPresenter> implements IGraduationInterface{
    @Inject
    GraduMainFragment   mGraduMainFragment;
    @Inject
    GraduCreateFragment mGraduCreateFragment;
    @Inject
    GraduJoinFragment   mGraduJoinFragment;

//    @BindView(R.id.toolbar)
//    Toolbar mToolbar;

    public static void launch(Context context) {
        Intent intent = new Intent(context, GraduationActivity.class);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.fade_entry, R.anim.hold);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_graduation;
    }

    @Override
    protected void doInject() {
        DaggerGraduationComponent.builder()
                .graduationModule(new GraduationModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void doInit() {
        addFragment(R.id.container, mGraduMainFragment);
    }

    @Override
    protected void getData(boolean isRefresh) {

    }

    @Override
    public void switchToCreate() {
        replaceFragment(R.id.container, mGraduCreateFragment);
    }

    @Override
    public void switchToJoin() {
        replaceFragment(R.id.container, mGraduJoinFragment);
    }

    @Override
    public void switchToBack() {
        finish();
    }

    @Override
    public void switchToMain() {
        Utils.enterMain(this);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.fade_exit);
    }
}
