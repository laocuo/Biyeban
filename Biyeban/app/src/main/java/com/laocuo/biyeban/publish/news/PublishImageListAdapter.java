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

package com.laocuo.biyeban.publish.news;


import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.utils.Utils;
import com.laocuo.recycler.adapter.BaseMultiItemQuickAdapter;
import com.laocuo.recycler.adapter.BaseViewHolder;

import javax.inject.Inject;


public class PublishImageListAdapter extends BaseMultiItemQuickAdapter<ImageItem> {
    private Context mContext;

    @Inject
    PublishImageListAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void attachItemType() {
        addItemType(ImageItem.ITEM_TYPE_NORMAL, R.layout.image_list_item);
    }

    @Override
    protected void convert(BaseViewHolder holder, ImageItem item) {
        switch (item.getItemType()) {
            case ImageItem.ITEM_TYPE_NORMAL:
                _handleNewsNormal(holder, item);
                break;
        }
    }

    private void _handleNewsNormal(BaseViewHolder holder, ImageItem item) {
        if (TextUtils.isEmpty(item.getImgUrl())) {
            ((ImageView) holder.getView(R.id.pic)).setImageResource(R.drawable.add_img);
        } else {
            Utils.setImage(mContext, item.getImgUrl(), (ImageView) holder.getView(R.id.pic));
        }
    }
}
