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

package com.laocuo.biyeban.main.contacts;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.greendao.DaoSession;
import com.laocuo.biyeban.greendao.UserDao;
import com.laocuo.biyeban.utils.BmobUtils;
import com.laocuo.biyeban.utils.FactoryInterface;
import com.laocuo.recycler.adapter.BaseMultiItemQuickAdapter;
import com.laocuo.recycler.adapter.BaseViewHolder;


import java.util.List;

import javax.inject.Inject;


public class ContactsListAdapter extends BaseMultiItemQuickAdapter<ContactsItem> {
    private Context mContext;
    private UserDao mUserDao;

    @Inject
    ContactsListAdapter(Context context, DaoSession daosession) {
        super(context);
        mContext = context;
        mUserDao = daosession.getUserDao();
    }

    @Override
    protected void attachItemType() {
        addItemType(ContactsItem.ITEM_TYPE_NORMAL, R.layout.contacts_list_item);
    }

    @Override
    protected void convert(BaseViewHolder holder, ContactsItem item) {
        switch (item.getItemType()) {
            case ContactsItem.ITEM_TYPE_NORMAL:
                _handleContactsNormal(holder, item);
                break;
        }
    }

    private void _handleContactsNormal(BaseViewHolder holder, ContactsItem item) {
        TextView alias = holder.getView(R.id.alias);
        alias.setText(item.getAlias());

        ImageView avatar = holder.getView(R.id.avatar);
        FactoryInterface.setAvatar(mContext, item.getAvatar(), avatar);

        TextView contact_head = holder.getView(R.id.contact_head);
        List<ContactsItem> list = getData();
        int position = list.indexOf(item);
        String head = list.get(position).getPinyin().substring(0, 1);
        String prev_head = null;
        if (position > 0) {
            prev_head = list.get(position - 1).getPinyin().substring(0, 1);
            if (head.equals(prev_head)) {
                contact_head.setVisibility(View.GONE);
                return;
            }
        }
        contact_head.setText(head);
        contact_head.setVisibility(View.VISIBLE);

        BmobUtils.updateUserItem(item, mUserDao);
    }
}
