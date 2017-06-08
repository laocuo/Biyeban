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

package com.laocuo.biyeban.graduation.main;

import android.widget.Button;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.graduation.IGraduationInterface;

import butterknife.BindView;
import butterknife.OnClick;

public class GraduMainFragment extends BaseFragment {
    private IGraduationInterface mIGraduationInterface;

    @BindView(R.id.create_class)
    Button mButtonCreate;
    @BindView(R.id.join_class)
    Button mButtonJoin;

    public static GraduMainFragment newInstance(IGraduationInterface anInterface) {
        GraduMainFragment fragment = new GraduMainFragment();
        fragment.setIGraduationInterface(anInterface);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_gradumain;
    }

    @Override
    protected void doInject() {

    }

    @Override
    protected void doInit() {

    }

    @Override
    protected void getData(boolean isRefresh) {

    }

    @OnClick(R.id.create_class)
    void onCreateClass() {
        mIGraduationInterface.switchToCreate();
    }

    @OnClick(R.id.join_class)
    void onJoinClass() {
        mIGraduationInterface.switchToJoin();
    }

    public void setIGraduationInterface(IGraduationInterface IGraduationInterface) {
        mIGraduationInterface = IGraduationInterface;
    }
}
