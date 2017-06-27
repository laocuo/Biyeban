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
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.utils.FactoryInterface;
import com.laocuo.biyeban.utils.L;
import com.laocuo.recycler.adapter.BaseMultiItemQuickAdapter;
import com.laocuo.recycler.adapter.BaseViewHolder;

import javax.inject.Inject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;


public class FreshNewsListAdapter  extends BaseMultiItemQuickAdapter<FreshNewsItem> {

    @Inject
    public FreshNewsListAdapter(Context context) {
        super(context);
    }

    @Override
    protected void attachItemType() {
        addItemType(FreshNewsItem.ITEM_TYPE_NORMAL, R.layout.freshnews_list_item);
    }

    @Override
    protected void convert(BaseViewHolder holder, FreshNewsItem item) {
        switch (item.getItemType()) {
            case FreshNewsItem.ITEM_TYPE_NORMAL:
                _handleNewsNormal(holder, item);
                break;
        }
    }

    private void _handleNewsNormal(final BaseViewHolder holder, final FreshNewsItem item) {
        TextView c = holder.getView(R.id.tv_content);
        c.setText(item.getContent());
        TextView t = holder.getView(R.id.tv_time);
        t.setText(item.getTime());
        final TextView n = holder.getView(R.id.tv_name);
        final ImageView a = holder.getView(R.id.tv_avatar);
        if (!TextUtils.isEmpty(item.getUserObjectId())) {
            BmobQuery<BiyebanUser> query = new BmobQuery<BiyebanUser>();
            query.getObject(item.getUserObjectId(), new QueryListener<BiyebanUser>() {

                @Override
                public void done(BiyebanUser user, BmobException e) {
                    if (user != null) {
                        String name = TextUtils.isEmpty(user.getAlias()) ? user.getUsername() : user.getAlias();
                        n.setText(name);
                        String avatar = user.getAvatar() != null ? user.getAvatar().getFileUrl() : null;
                        if (avatar != null && !TextUtils.isEmpty(avatar)) {
                            FactoryInterface.setAvatar(mContext, avatar, a);
                        } else {
                            a.setImageResource(R.drawable.user);
                        }
                    } else {
                        L.d("user == null");
                    }
                }
            });
        }
    }
}
