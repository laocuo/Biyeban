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
import android.widget.TextView;

import com.laocuo.recycler.entity.MultiItemEntity;
import com.laocuo.recycler.helper.RecyclerViewHelper;

import java.util.ArrayList;


public class FreshNewsItem extends MultiItemEntity {
    public static final int ITEM_TYPE_NORMAL = 1;
    private String userObjectId;
    private String content;
    private String time;
    private ArrayList<String> pics = null;
    private FreshNewsImagesAdapter mImgsAdapter = null;

    public FreshNewsItem(int itemType,
                         String userObjectId,
                         String content,
                         String time,
                         ArrayList<String> pics) {
        super(itemType);
        this.userObjectId = userObjectId;
        this.content = content;
        this.time = time;
        this.pics = pics;
    }

    public String getUserObjectId() {
        return userObjectId;
    }

    public String getTime() {
        return time;
    }

    public void bindNormal(Context context, TextView c, RecyclerView r) {
        if (!TextUtils.isEmpty(content)) {
            c.setText(content);
            c.setVisibility(View.VISIBLE);
        } else {
            c.setText("");
            c.setVisibility(View.GONE);
        }
        if (pics != null && pics.size() > 0) {
            if (mImgsAdapter == null) {
                mImgsAdapter = new FreshNewsImagesAdapter(context);
            }
            RecyclerViewHelper.initRecyclerViewH(context, r, false, mImgsAdapter);
            mImgsAdapter.setData(pics);
        } else {
            if (mImgsAdapter != null) {
                mImgsAdapter.setData(null);
            }
            r.setAdapter(null);
        }
    }
}