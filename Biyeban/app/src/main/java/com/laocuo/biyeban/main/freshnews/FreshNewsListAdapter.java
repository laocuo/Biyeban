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
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.recycler.adapter.BaseMultiItemQuickAdapter;
import com.laocuo.recycler.adapter.BaseViewHolder;

import javax.inject.Inject;


public class FreshNewsListAdapter  extends BaseMultiItemQuickAdapter<FreshNewsItem> {

    @Inject
    public FreshNewsListAdapter(Context context) {
        super(context);
    }

    @Override
    protected void attachItemType() {
        addItemType(FreshNewsItem.ITEM_TYPE_NORMAL, R.layout.adapter_freshnews_list);
    }

    @Override
    protected void convert(BaseViewHolder holder, FreshNewsItem item) {
        switch (item.getItemType()) {
            case FreshNewsItem.ITEM_TYPE_NORMAL:
                _handleNewsNormal(holder, item.getContent());
                break;
        }
    }

    private void _handleNewsNormal(final BaseViewHolder holder, final String content) {
        TextView tv = holder.getView(R.id.test_info);
        tv.setText(content);
    }
}
