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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.greendao.DaoSession;
import com.laocuo.biyeban.greendao.UserDao;
import com.laocuo.biyeban.utils.BmobUtils;

import com.laocuo.recycler.adapter.BaseMultiItemQuickAdapter;
import com.laocuo.recycler.adapter.BaseViewHolder;

import javax.inject.Inject;


public class FreshNewsListAdapter  extends BaseMultiItemQuickAdapter<FreshNewsItem> {
    private Context mContext;
    private UserDao mUserDao;

    @Inject
    FreshNewsListAdapter(Context context, DaoSession daosession) {
        super(context);
        mContext = context;
        mUserDao = daosession.getUserDao();
    }

    @Override
    protected void attachItemType() {
        addItemType(FreshNewsItem.ITEM_TYPE_MULTI_IMAGES, R.layout.freshnews_list_item_multi_images);
        addItemType(FreshNewsItem.ITEM_TYPE_SINGLE_IMAGE, R.layout.freshnews_list_item_single_image);
    }

    @Override
    protected void convert(BaseViewHolder holder, FreshNewsItem item) {
        switch (item.getItemType()) {
            case FreshNewsItem.ITEM_TYPE_MULTI_IMAGES:
                _handleNewsMultiImages(holder, item);
                break;
            case FreshNewsItem.ITEM_TYPE_SINGLE_IMAGE:
                _handleNewsSingleImage(holder, item);
                break;
        }
    }

    private void _handleNewsMultiImages(final BaseViewHolder holder, final FreshNewsItem item) {
        TextView n = holder.getView(R.id.tv_name);
        ImageView a = holder.getView(R.id.tv_avatar);
        TextView c = holder.getView(R.id.tv_content);
        TextView t = holder.getView(R.id.tv_time);
        RecyclerView r = holder.getView(R.id.images);

        BmobUtils.bindUserItems(n, a, item.getUserObjectId(), mUserDao, mContext);
        if (!TextUtils.isEmpty(item.getContent())) {
            c.setText(item.getContent());
            c.setVisibility(View.VISIBLE);
        } else {
            c.setText("");
            c.setVisibility(View.GONE);
        }
        t.setText(item.getTime());

        item.bindMultiImages(mContext, r);
    }

    private void _handleNewsSingleImage(final BaseViewHolder holder, final FreshNewsItem item) {
        TextView n = holder.getView(R.id.tv_name);
        ImageView a = holder.getView(R.id.tv_avatar);
        TextView c = holder.getView(R.id.tv_content);
        TextView t = holder.getView(R.id.tv_time);
        ImageView i = holder.getView(R.id.image);

        BmobUtils.bindUserItems(n, a, item.getUserObjectId(), mUserDao, mContext);
        if (!TextUtils.isEmpty(item.getContent())) {
            c.setText(item.getContent());
            c.setVisibility(View.VISIBLE);
        } else {
            c.setText("");
            c.setVisibility(View.GONE);
        }
        t.setText(item.getTime());

        item.bindSingleImage(mContext, i);
    }
}
