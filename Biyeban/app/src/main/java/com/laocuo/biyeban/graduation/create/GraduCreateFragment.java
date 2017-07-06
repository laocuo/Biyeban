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


import android.app.ProgressDialog;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;

import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.bmob.Chat;
import com.laocuo.biyeban.bmob.FreshNews;
import com.laocuo.biyeban.bmob.GraduClass;
import com.laocuo.biyeban.graduation.IGraduationInterface;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.SnackbarUtil;
import com.laocuo.biyeban.utils.Utils;
import com.laocuo.biyeban.widget.EasyPickerView;
import com.lljjcoder.citypickerview.widget.CityPicker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class GraduCreateFragment extends BaseFragment {
    private IGraduationInterface mIGraduationInterface;

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

    public static GraduCreateFragment newInstance(IGraduationInterface anInterface) {
        GraduCreateFragment fragment = new GraduCreateFragment();
        fragment.setIGraduationInterface(anInterface);
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
        mProgressDialog = new ProgressDialog(mContext);
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
            yearList.add("" + i + "届");
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
                mDistrict.setText(citySelected[0]
                        + citySelected[1]
                        + citySelected[2]);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @OnClick(R.id.create_class)
    void createClass() {
        String name = mEditName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            SnackbarUtil.showShortSnackbar(mCreateClass, "班级名不能为空");
            return;
        }
        BiyebanUser user = BmobUser.getCurrentUser(BiyebanUser.class);
        if (user != null) {
            showProgress(true);
            ArrayList<String> classmates = new ArrayList<>();
            String year = mGraduYear.getText().toString();
            String district = mDistrict.getText().toString();
            classmates.add(user.getObjectId());
            GraduClass graduClass = new GraduClass();
            graduClass.setAdmin(user.getObjectId());
            graduClass.setClassName(name);
            graduClass.setGraduYear(year);
            graduClass.setDistrict(district);
            graduClass.setClassmates(classmates);
            graduClass.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        SnackbarUtil.showShortSnackbar(mCreateClass, "班级创建成功");
                        BmobQuery<GraduClass> query = new BmobQuery<>();
                        query.getObject(s, new QueryListener<GraduClass>() {
                            @Override
                            public void done(GraduClass aClass, BmobException e) {
                                if (e == null) {
                                    createGraduClassChatRoom(aClass.getAdmin(), aClass.getObjectId());
                                    createGraduClassFreshNews(aClass.getAdmin(), aClass.getObjectId());
                                    updateCurrentUser(aClass);
                                } else {
                                    L.d(e.toString());
                                }
                            }
                        });
                    } else {
                        L.d(e.toString());
                    }
                }
            });
        }
    }

    private void updateCurrentUser(GraduClass gc) {
        final BiyebanUser user = BmobUser.getCurrentUser(BiyebanUser.class);
        if (user != null) {
            user.setGraduClass(gc.getObjectId());
            BiyebanUser u = new BiyebanUser();
            u.setGraduClass(gc.getObjectId());
            u.update(user.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    showProgress(false);
                    if (e == null) {
                        //success, jump to MainActivity
                        mIGraduationInterface.switchToMain();
                    } else {
                        L.d(e.toString());
                    }
                }
            });
        }
    }

    public void setIGraduationInterface(IGraduationInterface IGraduationInterface) {
        mIGraduationInterface = IGraduationInterface;
    }

    private void createGraduClassChatRoom(String user_obj, String class_obj) {
        Chat chat = new Chat(user_obj, Utils.CHATROOM + class_obj);
        chat.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e != null) {
                    L.d(e.toString());
                }
            }
        });
    }

    private void createGraduClassFreshNews(String user_obj, String class_obj) {
        FreshNews freshNews = new FreshNews(user_obj, Utils.FRESHNEWS + class_obj);
        freshNews.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e != null) {
                    L.d(e.toString());
                }
            }
        });
    }
}
