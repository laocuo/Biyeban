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

package com.laocuo.biyeban.main.chatroom;


import android.content.Context;
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


public class ChatListAdapter extends BaseMultiItemQuickAdapter<ChatItem> {

    private Context mContext;
    private UserDao mUserDao;

    @Inject
    ChatListAdapter(Context context, DaoSession daosession) {
        super(context);
        this.mContext = context;
        mUserDao = daosession.getUserDao();
    }

    @Override
    protected void attachItemType() {
        addItemType(ChatItem.ITEM_TYPE_SEND, R.layout.chat_list_item_send);
        addItemType(ChatItem.ITEM_TYPE_RECV, R.layout.chat_list_item_recv);
    }

    @Override
    protected void convert(BaseViewHolder holder, ChatItem item) {
        switch (item.getItemType()) {
            case ChatItem.ITEM_TYPE_SEND:
            case ChatItem.ITEM_TYPE_RECV:
                _handleChatCommon(holder, item);
                break;
        }
    }

    private void _handleChatCommon(BaseViewHolder holder, ChatItem item) {
        TextView tv_name = holder.getView(R.id.tv_name);
        TextView tv_content = holder.getView(R.id.tv_content);
        TextView tv_time = holder.getView(R.id.tv_time);
        ImageView tv_avatar = holder.getView(R.id.tv_avatar);

        ChatItem chat = item;

        BmobUtils.bindUserItems(tv_name, tv_avatar, chat.getUserObjectId(), mUserDao, mContext);

        tv_content.setText(chat.getContent());
        String time = chat.getTime();
        if (time != null && !TextUtils.isEmpty(time)) {
            int position = getData().indexOf(item);
            if (position > 0 &&
                    chat.getTime() != null &&
                    chat.getTime().equals(((ChatItem) (getItem(position-1))).getTime())) {
                tv_time.setVisibility(View.GONE);
            } else {
                tv_time.setText(chat.getTime());
                tv_time.setVisibility(View.VISIBLE);
            }
        } else {
            tv_time.setVisibility(View.GONE);
        }
    }
}
