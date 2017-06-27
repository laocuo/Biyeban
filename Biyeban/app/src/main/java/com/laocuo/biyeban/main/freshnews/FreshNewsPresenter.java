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

package com.laocuo.biyeban.main.freshnews;


import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class FreshNewsPresenter implements IFreshNewsPresenter {
    private IFreshNewsView mIFreshNewsView;
    private List<FreshNewsItem> mFreshNewsItems = new ArrayList<>();
    private List<FreshNewsItem> mFreshNewsMoreItems = new ArrayList<>();
    private int index = 0;
    private BiyebanUser user = Utils.getCurrentUser();
    private String freshNewsTableName;

    @Override
    public void loadData() {
        mIFreshNewsView.showLoading();
        freshNewsTableName = user.getGraduClass().getObjectId() + Utils.FRESHNEWS;
        L.d("freshNewsTableName = "+freshNewsTableName);
        mFreshNewsItems.clear();
        BmobQuery bmobQuery = new BmobQuery(freshNewsTableName);
        bmobQuery.order("-createdAt");
        bmobQuery.setLimit(20);
        bmobQuery.findObjectsByTable(new QueryListener<JSONArray>() {

            @Override
            public void done(JSONArray array, BmobException e) {
                if (e == null) {
                    int len = array.length();
                    L.d("array.length()"+len);
                    for (int i=0;i<len;i++) {
                        JSONObject data = null;
                        try {
                            data = array.getJSONObject(i);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        L.d(i+"data:"+data.toString());
                        mFreshNewsItems.add(new FreshNewsItem(
                                FreshNewsItem.ITEM_TYPE_NORMAL,
                                data.optString("userObjectId"),
                                data.optString("content"),
                                data.optString("time"),
                                null));
                    }
                    if (mFreshNewsItems.size() > 0) {
                        mIFreshNewsView.loadData(mFreshNewsItems);
                    }
                } else {
                    L.d(e.toString());
                }
                mIFreshNewsView.hideLoading();
            }
        });

    }

    @Override
    public void loadMoreData() {

    }

    @Inject
    FreshNewsPresenter(IFreshNewsView view) {
        mIFreshNewsView = view;
    }

    @Override
    public void swipeRefresh() {
        loadData();
    }
}
