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

package com.laocuo.biyeban.main.contacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.Utils;
import com.laocuo.recycler.helper.RecyclerViewHelper;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;


public class ContactsFragment extends BaseFragment<ContactsPresenter>
        implements IContactsView{
    private static final String TYPE_KEY = "TypeKey";
    private String mTitle;

    @Inject
    ContactsListAdapter mAdapter;

    @BindView(R.id.contacts_list)
    RecyclerView mRecyclerView;

    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(TYPE_KEY);
        }
        L.d("ContactsFragment:onCreate");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contacts;
    }

    @Override
    protected void doInject() {
        DaggerContactsComponent.builder()
                .contactsModule(new ContactsModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void doInit() {
        RecyclerViewHelper.initRecyclerViewV(mContext, mRecyclerView, true, mAdapter);
        mPresenter.setClassMates(Utils.getCurrentUser().getGraduClass().getClassmates());
    }

    @Override
    protected void getData(boolean isRefresh) {
        if (isRefresh == true) {
//            mPresenter.loadMoreData();
        } else {
            mPresenter.loadData();
        }
    }

    @Override
    public void loadData(List<ContactsItem> data) {
        mAdapter.updateItems(data);
    }

    @Override
    public void loadMoreData(List<ContactsItem> data) {

    }

    @Override
    public void loadNoData() {

    }
}
