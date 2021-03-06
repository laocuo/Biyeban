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
import com.laocuo.biyeban.bmob.FreshNews;
import com.laocuo.biyeban.utils.BmobUtils;
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
import cn.bmob.v3.listener.UpdateListener;

public class FreshNewsPresenter implements IFreshNewsPresenter {
    public static final int STEP  = 10;
    private IFreshNewsView mIFreshNewsView;
    private List<FreshNewsItem> mFreshNewsItems = new ArrayList<>();
    private List<FreshNewsItem> mFreshNewsMoreItems = new ArrayList<>();
    private BiyebanUser user = BmobUtils.getCurrentUser();
    private String freshNewsTableName;
    private int skip = 0;

    @Inject
    FreshNewsPresenter(IFreshNewsView view) {
        mIFreshNewsView = view;
        freshNewsTableName = Utils.FRESHNEWS + user.getGraduClass();
        L.d("freshNewsTableName = "+freshNewsTableName);
    }

    @Override
    public void loadData() {
        mIFreshNewsView.showLoading();
        BmobQuery bmobQuery = new BmobQuery(freshNewsTableName);
        bmobQuery.order("-createdAt");
        bmobQuery.setLimit(STEP);
        bmobQuery.setSkip(skip * STEP);
        skip++;
//        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        boolean isCache = bmobQuery.hasCachedResult(FreshNews.class);
        L.d("loadData isCache="+isCache);
        bmobQuery.findObjectsByTable(new QueryListener<JSONArray>() {

            @Override
            public void done(JSONArray array, BmobException e) {
                if (e == null) {
                    mFreshNewsItems.clear();
                    mFreshNewsItems.addAll(parseJsonArray(array));
                    if (mFreshNewsItems.size() > 0) {
                        mIFreshNewsView.loadData(mFreshNewsItems);
                    } else {
                        mIFreshNewsView.loadNoData();
                    }
                } else {
                    L.d(e.toString());
                }
                mIFreshNewsView.hideLoading();
            }
        });
    }

    private List<FreshNewsItem> parseJsonArray(JSONArray array) {
        List<FreshNewsItem> newsItems = new ArrayList<>();
        int len = array.length();
        for (int i = 0;i < len;i++) {
            JSONObject data = null;
            try {
                data = array.getJSONObject(i);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            ArrayList<String> mPics = null;
            JSONArray pics = data.optJSONArray("pics");
            if (pics != null && pics.length() > 0) {
                mPics = new ArrayList<>();
                for (int j = 0;j < pics.length();j++) {
                    try {
                        String img = (String) pics.get(j);
                        if (img != null) {
                            mPics.add(img);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            ArrayList<String> mComments = null;
            JSONArray comments = data.optJSONArray("comments");
            if (comments != null && comments.length() > 0) {
                mComments = new ArrayList<>();
                for (int j = 0;j < comments.length();j++) {
                    try {
                        String comment = (String) comments.get(j);
                        if (comment != null) {
                            mComments.add(comment);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            newsItems.add(new FreshNewsItem(
                    data.optString("objectId"),
                    freshNewsTableName,
                    mPics != null && mPics.size() == 1 ? FreshNewsItem.ITEM_TYPE_SINGLE_IMAGE : FreshNewsItem.ITEM_TYPE_MULTI_IMAGES,
                    data.optString("userObjectId"),
                    data.optString("content"),
                    data.optString("time"),
                    mPics,
                    mComments));
        }
        return newsItems;
    }

    @Override
    public void loadMoreData() {
        BmobQuery bmobQuery = new BmobQuery(freshNewsTableName);
        bmobQuery.order("-createdAt");
        bmobQuery.setLimit(STEP);
        bmobQuery.setSkip(skip * STEP);
        skip++;
        boolean isCache = bmobQuery.hasCachedResult(FreshNews.class);
        L.d("loadMoreData isCache="+isCache);
        bmobQuery.findObjectsByTable(new QueryListener<JSONArray>() {

            @Override
            public void done(JSONArray array, BmobException e) {
                if (e == null) {
                    mFreshNewsMoreItems.clear();
                    mFreshNewsMoreItems.addAll(parseJsonArray(array));
                    if (mFreshNewsMoreItems.size() > 0) {
                        mIFreshNewsView.loadMoreData(mFreshNewsMoreItems);
                    }
                } else {
                    L.d(e.toString());
                }
            }
        });
    }

    @Override
    public void swipeRefresh() {
        skip = 0;
        BmobQuery bmobQuery = new BmobQuery(freshNewsTableName);
        bmobQuery.order("-createdAt");
        bmobQuery.setLimit(STEP);
        bmobQuery.setSkip(skip * STEP);
        skip++;
//        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        boolean isCache = bmobQuery.hasCachedResult(FreshNews.class);
        L.d("loadData isCache="+isCache);
        bmobQuery.findObjectsByTable(new QueryListener<JSONArray>() {

            @Override
            public void done(JSONArray array, BmobException e) {
                if (e == null) {
                    mFreshNewsItems.clear();
                    mFreshNewsItems.addAll(parseJsonArray(array));
                    if (mFreshNewsItems.size() > 0) {
                        mIFreshNewsView.loadData(mFreshNewsItems);
                    } else {
                        mIFreshNewsView.loadNoData();
                    }
                } else {
                    L.d(e.toString());
                }
            }
        });
    }

    @Override
    public void addComment(final String content, final FreshNewsItem item) {
        mIFreshNewsView.showProgress(true);
        FreshNews freshNews = new FreshNews(freshNewsTableName);
        ArrayList<String> comments = new ArrayList<>();
        ArrayList<String> current_comments = item.getComments();
        if (current_comments != null) {
            comments.addAll(current_comments);
        }
        final String comment = user.getObjectId() + "|" + content;
        comments.add(comment);
        freshNews.setComments(comments);
        freshNews.update(item.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                mIFreshNewsView.showProgress(false);
                if (e == null) {
                    item.addComment(comment);
                    mIFreshNewsView.addCommentClickResult(true);
                } else {
                    L.d(e.toString());
                    mIFreshNewsView.addCommentClickResult(false);
                }
            }
        });
    }
}
