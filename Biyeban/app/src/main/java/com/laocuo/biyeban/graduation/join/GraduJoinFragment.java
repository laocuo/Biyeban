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


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.graduation.IGraduationInterface;
import com.laocuo.biyeban.utils.L;
import com.lljjcoder.citypickerview.widget.CityPicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class GraduJoinFragment extends BaseFragment<GraduJoinPresenter> implements
        SearchView.OnQueryTextListener, IGraduJoinView {
    private IGraduationInterface mIGraduationInterface;

    @BindView(R.id.search_class)
    SearchView mSearchView;
    @BindView(R.id.class_list)
    ListView mListView;
    @BindView(R.id.empty_content)
    LinearLayout mEmptyLayout;
    @BindView(R.id.select_district)
    RelativeLayout mSelectDistrict;
    @BindView(R.id.district)
    TextView mDistrict;

    private List<HashMap<String, String>> mClassList = new ArrayList<>();
    private MySimpleAdapter simpleAdapter;
    private View.OnClickListener mOnClickListener;
    private CityPicker cityPicker;

    public static GraduJoinFragment newInstance(IGraduationInterface anInterface) {
        GraduJoinFragment fragment = new GraduJoinFragment();
        fragment.setIGraduationInterface(anInterface);
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
        mProgressDialog = new ProgressDialog(mContext);
//        mSearchView.setIconifiedByDefault(false);
        mSearchView.onActionViewExpanded();
//        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.clearFocus();
        mListView.setEmptyView(mEmptyLayout);
//        mListView.setTextFilterEnabled(true);
        simpleAdapter = new MySimpleAdapter(mContext, mClassList,
                R.layout.graduclass_list_item,
                new String[]{"name","district","year"},
                new int[]{R.id.gradu_class_name,R.id.gradu_district,R.id.gradu_year});
        mListView.setAdapter(simpleAdapter);
//        mListView.setOnItemClickListener(this);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                joinGraduClass(position);
            }
        };
        initCityPicker();
    }

    @Override
    protected void getData(boolean isRefresh) {
    }

    @OnClick(R.id.select_district)
    void selectDistrict() {
        cityPicker.show();
    }

    private void initCityPicker() {
        cityPicker = new CityPicker.Builder(getContext()).textSize(20)
                .titleTextColor("#000000")
                .backgroundPop(0xa0000000)
                .province("江苏省")
                .city("南京市")
                .district("秦淮区")
                .textColor(Color.parseColor("#FF4081"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .build();
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                mDistrict.setText(citySelected[0]
                        + citySelected[1]
                        + citySelected[2]);
                showProgress(true);
                mPresenter.queryGraduClass(mDistrict.getText().toString());
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        mListView.setFilterText(newText);
        if (simpleAdapter instanceof Filterable) {
            Filter filter = simpleAdapter.getFilter();
            if (TextUtils.isEmpty(newText)) {
                filter.filter(null);
            } else {
                filter.filter(newText);
            }
        }
        return false;
    }

    @Override
    public void loadData(List<HashMap<String, String>> data) {
        showProgress(false);
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

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        joinGraduClass(position);
//    }

    private void joinGraduClass(int position) {
        L.d("onItemClick : "+position);
        showProgress(true);
        mPresenter.joinGraduClass(position);
    }

    @Override
    public void joinGraduClass(boolean ret) {
        showProgress(false);
        if (ret == true) {
            //todo jump into mainactivity
            mIGraduationInterface.switchToMain();
        }
    }

    public void setIGraduationInterface(IGraduationInterface IGraduationInterface) {
        mIGraduationInterface = IGraduationInterface;
    }

    private class MySimpleAdapter extends SimpleAdapter {

        /**
         * Constructor
         *
         * @param context  The context where the View associated with this SimpleAdapter is running
         * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
         *                 Maps contain the data for each row, and should include all the entries specified in
         *                 "from"
         * @param resource Resource identifier of a view layout that defines the views for this list
         *                 item. The layout file should include at least those named views defined in "to"
         * @param from     A list of column names that will be added to the Map associated with each
         *                 item.
         * @param to       The views that should display column in the "from" parameter. These should all be
         *                 TextViews. The first N views in this list are given the values of the first N columns
         */
        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            Button b = (Button) v.findViewById(R.id.join_class);
            b.setTag(position);
            b.setOnClickListener(mOnClickListener);
            return v;
        }
    }
}
