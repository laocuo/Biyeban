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

package com.laocuo.biyeban.graduation.join;


import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseActivity;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class GraduJoinFragment extends BaseFragment<GraduJoinPresenter> implements
        SearchView.OnQueryTextListener, IGraduJoinView, AdapterView.OnItemClickListener {

    @BindView(R.id.search_class)
    SearchView mSearchView;
    @BindView(R.id.class_list)
    ListView mListView;
    @BindView(R.id.empty_content)
    LinearLayout mEmptyLayout;

    private List<HashMap<String, String>> mClassList = new ArrayList<>();
    private SimpleAdapter simpleAdapter;

    public static GraduJoinFragment newInstance() {
        GraduJoinFragment fragment = new GraduJoinFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_gradujoin;
    }

    @Override
    protected void doInject() {
        DaggerGraduJoinComponent.builder()
                .graduJoinModule(new GraduJoinModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void doInit() {
//        mSearchView.setIconifiedByDefault(false);
        mSearchView.onActionViewExpanded();
//        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);
        mListView.setEmptyView(mEmptyLayout);
        mListView.setTextFilterEnabled(true);
        simpleAdapter = new SimpleAdapter(mContext, mClassList,
                android.R.layout.simple_list_item_1,
                new String[]{"name"},
                new int[]{android.R.id.text1});
        mListView.setAdapter(simpleAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void getData(boolean isRefresh) {
        mPresenter.loadData();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mListView.setFilterText(newText);
        return false;
    }

    @Override
    public void loadData(List<HashMap<String, String>> data) {
        mClassList.clear();
        mClassList.addAll(data);
        simpleAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadMoreData(List<HashMap<String, String>> data) {

    }

    @Override
    public void loadNoData() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.joinGraduClass(position);
    }

    @Override
    public void joinGraduClass(boolean ret) {
        if (ret == true) {
            //todo jump into mainactivity
            Utils.enterMain((BaseActivity) getActivity());
        }
    }
}
