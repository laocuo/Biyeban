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

package com.laocuo.biyeban.graduation.create;


import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;

import com.laocuo.biyeban.widget.EasyPickerView;
import com.lljjcoder.citypickerview.widget.CityPicker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class GraduCreateFragment extends BaseFragment {

    @BindView(R.id.edit_class_name)
    EditText mEditName;
    @BindView(R.id.select_gradu_year)
    RelativeLayout mSelectYear;
    @BindView(R.id.gradu_year)
    TextView mGraduYear;
    @BindView(R.id.year_pick)
    EasyPickerView mYearPicker;
    @BindView(R.id.select_district)
    RelativeLayout mSelectDistrict;
    @BindView(R.id.district)
    TextView mDistrict;
    @BindView(R.id.create_class)
    Button mCreateClass;

    final ArrayList<String> yearList = new ArrayList<>();
    private CityPicker cityPicker;

    public static GraduCreateFragment newInstance() {
        GraduCreateFragment fragment = new GraduCreateFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_graducreate;
    }

    @Override
    protected void doInject() {

    }

    @Override
    protected void doInit() {
        initYearPicker();
        initCityPicker();
    }

    @Override
    protected void getData(boolean isRefresh) {

    }

    @OnClick(R.id.select_gradu_year)
    void selectGraduYear() {
        if (mYearPicker.getVisibility() == View.VISIBLE) {
            mYearPicker.setVisibility(View.GONE);
        } else {
            mYearPicker.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.select_district)
    void selectDistrict() {
        cityPicker.show();
    }

    private void initYearPicker() {
        for (int i = 1976; i < 2026; i++)
            yearList.add("" + i);
        mYearPicker.setDataList(yearList);
        mYearPicker.setOnScrollChangedListener(new EasyPickerView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int curIndex) {
                mGraduYear.setText(yearList.get(curIndex));
            }

            @Override
            public void onScrollFinished(int curIndex) {
                mGraduYear.setText(yearList.get(curIndex));
            }
        });
    }

    private void initCityPicker() {
        cityPicker = new CityPicker.Builder(getContext()).textSize(20)
                .titleTextColor("#000000")
                .backgroundPop(0xa0000000)
                .province("江苏省")
                .city("南京市")
                .district("秦淮区")
                .textColor(Color.parseColor("#4aa4a4"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(10)
                .build();
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                mDistrict.setText(citySelected[0] + "\n"
                        + citySelected[1] + "\n"
                        + citySelected[2]);
//                mDistrict.setText(citySelected[0]
//                        + citySelected[1]
//                        + citySelected[2]);
            }

            @Override
            public void onCancel() {

            }
        });
    }
}
