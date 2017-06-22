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
import com.laocuo.biyeban.bmob.Chat;
import com.laocuo.biyeban.utils.FactoryInterface;

import java.util.List;

import javax.inject.Inject;

public class ChatListAdapter extends BaseAdapter {

    private List<Chat> messages;
    private ViewHolder holder;
    private Context mContext;
    private String mCurrentUserName;

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
    ChatListAdapter(Context context, String currentUserName) {
        this.mContext = context;
        this.mCurrentUserName = currentUserName;
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
        String username = messages.get(position).getUsername();
        if (username != null && mCurrentUserName.contentEquals(username)) {
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
        if (chat.getUsername().equalsIgnoreCase("admin")) {
            StringBuilder display_name = new StringBuilder(chat.getName());
            display_name.append("(").append(chat.getUsername()).append(")");
            holder.tv_name.setText(display_name);
        } else {
            holder.tv_name.setText(chat.getName());
        }
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
        String avatar = chat.getAvatar();
        if (avatar != null && !TextUtils.isEmpty(avatar)) {
            FactoryInterface.setAvatar(mContext, chat.getAvatar(), holder.tv_avatar);
        } else {
            holder.tv_avatar.setImageResource(R.drawable.user);
        }

        return convertView;
    }

    class ViewHolder{
        TextView tv_name;
        TextView tv_content;
        TextView tv_time;
        ImageView tv_avatar;
    }
}
