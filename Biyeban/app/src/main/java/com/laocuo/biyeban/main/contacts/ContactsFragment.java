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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.widget.CityNavigateView;
import com.laocuo.recycler.helper.RecyclerViewHelper;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;


public class ContactsFragment extends BaseFragment<ContactsPresenter>
        implements IContactsView, CityNavigateView.onTouchListener {
    private static final String TYPE_KEY = "TypeKey";
    private String mTitle;

    @Inject
    ContactsListAdapter mAdapter;

    @BindView(R.id.contacts_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.contacts_navigate)
    CityNavigateView mNavigateView;
    @BindView(R.id.chief)
    TextView mChiefView;

    private List<ContactsItem> mContactsItemList;
    private LinearLayoutManager layoutManager;

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
        RecyclerViewHelper.initRecyclerViewV(mContext, mRecyclerView, false, mAdapter);
        mNavigateView.setListener(this);
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void getData(boolean isRefresh) {
        L.d("ContactsFragment:getData isRefresh="+isRefresh);
        if (isRefresh == true) {
//            mPresenter.loadMoreData();
            finishRefresh();
        } else {
            showLoading();
            mPresenter.getClassMates();
        }
    }

    @Override
    public void loadData(List<ContactsItem> data) {
        hideLoading();
        mContactsItemList = data;
        mAdapter.updateItems(data);
    }

    @Override
    public void loadMoreData(List<ContactsItem> data) {

    }

    @Override
    public void loadNoData() {

    }

    @Override
    public void loadNavigatorData(List<String> naviHeads, Set<String> naviHeadsSet) {
        mNavigateView.setContent(naviHeads);
    }

    @Override
    public void showChiefView(String text, boolean show) {
        if (show) {
            mChiefView.setText(text);
            mChiefView.setVisibility(View.VISIBLE);
            int selectPosition = 0;
            for (int i = 0; i < mContactsItemList.size(); i++) {
                if (mContactsItemList.get(i).getFirstPinYin().equals(text)) {
                    selectPosition = i;
                    break;
                }
            }
            scroll2Position(selectPosition);
        } else {
            mChiefView.setVisibility(View.INVISIBLE);
        }
    }

    private void scroll2Position(int index) {
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        int lastPosition = layoutManager.findLastVisibleItemPosition();
        if (index <= firstPosition) {
            mRecyclerView.scrollToPosition(index);
        } else if (index <= lastPosition) {
            int top = mRecyclerView.getChildAt(index - firstPosition).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.scrollToPosition(index);
        }
    }

}
