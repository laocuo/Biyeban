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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.bmob.Chat;
import com.laocuo.biyeban.main.freshnews.FreshNewsItem;
import com.laocuo.biyeban.utils.FactoryInterface;
import com.laocuo.biyeban.utils.L;
import com.laocuo.recycler.adapter.BaseViewHolder;

import java.util.List;

import javax.inject.Inject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class ChatListAdapter extends BaseAdapter {

    private List<Chat> messages;
    private ViewHolder holder;
    private Context mContext;
    private String mCurrentUserObjId;

    public void setChatList(List<Chat> list) {
        messages = list;
    }

    public List<Chat> getChatList() {
        return messages;
    }

    public void addChat(Chat c) {
        messages.add(c);
    }

    @Inject
    ChatListAdapter(Context context, String currentUserObjId) {
        this.mContext = context;
        this.mCurrentUserObjId = currentUserObjId;
    }

    public void setCurrentUserObjId(String currentUserObjId) {
        mCurrentUserObjId = currentUserObjId;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return messages.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        String userObjId = messages.get(position).getUserObjectId();
        if (userObjId != null && userObjId.equals(mCurrentUserObjId)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if(convertView == null){
            if (getItemViewType(position) == 0) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.chat_list_item_send, null);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.chat_list_item_recv, null);
            }
            holder = new ViewHolder();

            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_avatar = (ImageView) convertView.findViewById(R.id.tv_avatar);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Chat chat = messages.get(position);
        bindCommonItems(holder, chat.getUserObjectId());

        holder.tv_content.setText(chat.getContent());
        String time = chat.getTime();
        if (time != null && !TextUtils.isEmpty(time)) {
            if (position > 0 &&
                    chat.getTime() != null &&
                    chat.getTime().equals(messages.get(position - 1).getTime())) {
                holder.tv_time.setVisibility(View.GONE);
            } else {
                holder.tv_time.setText(chat.getTime());
                holder.tv_time.setVisibility(View.VISIBLE);
            }
        } else {
            holder.tv_time.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder{
        TextView tv_name;
        TextView tv_content;
        TextView tv_time;
        ImageView tv_avatar;
    }

    private void bindCommonItems(final ViewHolder holder, final String userObjId) {
        final TextView n = holder.tv_name;
        final ImageView a = holder.tv_avatar;
        if (!TextUtils.isEmpty(userObjId)) {
            BmobQuery<BiyebanUser> query = new BmobQuery<>();
            query.getObject(userObjId, new QueryListener<BiyebanUser>() {

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
