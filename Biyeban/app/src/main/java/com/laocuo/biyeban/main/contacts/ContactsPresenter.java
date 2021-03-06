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

import android.text.TextUtils;

import com.github.promeg.pinyinhelper.Pinyin;
import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.bmob.GraduClass;
import com.laocuo.biyeban.greendao.DaoSession;
import com.laocuo.biyeban.greendao.User;
import com.laocuo.biyeban.greendao.UserDao;
import com.laocuo.biyeban.utils.BmobUtils;
import com.laocuo.biyeban.utils.L;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;


public class ContactsPresenter implements IContactsPresenter {
    private IContactsView mIContactsView;
    private ArrayList<String> mClassMates;
    private List<ContactsItem> mContactsList = new ArrayList<>();
    private List<String> mNaviHeads = new ArrayList<String>();
    private Set<String> mNaviHeadsSet = new LinkedHashSet<>();
    private int count, length;
    private UserDao userDao;

    @Override
    public void loadData() {
        if (mClassMates == null) {
            return;
        }
        count = 0;
        length = mClassMates.size();
        L.d("ContactsPresenter loadData:"+mClassMates.toString());
        for (String objId : mClassMates) {
            QueryBuilder<User> qb = userDao.queryBuilder();
            qb.where(UserDao.Properties.Objid.eq(objId)).build();
            List<User> userList = qb.list();
            if (userList.size() <= 0) {
                BmobQuery<BiyebanUser> query = new BmobQuery<BiyebanUser>();
                query.getObject(objId, new QueryListener<BiyebanUser>() {
                    @Override
                    public void done(BiyebanUser user, BmobException e) {
                        if (e == null) {
                            String avatar = user.getAvatar() == null ? "" : user.getAvatar().getFileUrl();
                            String alias = TextUtils.isEmpty(user.getAlias()) ? user.getUsername() : user.getAlias();
                            mContactsList.add(new ContactsItem(ContactsItem.ITEM_TYPE_NORMAL,
                                    user.getUsername(),
                                    avatar,
                                    alias,
                                    transformPinYin(alias).toUpperCase(),
                                    user.getObjectId()));
                            userDao.insert(new User(null, user.getObjectId(), user.getUsername(),
                                    alias, avatar));
                        } else {
                            L.d(e.toString());
                        }
                        checkEnd();
                    }
                });
            } else {
                User user = userList.get(0);
                mContactsList.add(new ContactsItem(ContactsItem.ITEM_TYPE_NORMAL,
                        user.getUsername(),
                        user.getAvatar(),
                        user.getAlias(),
                        transformPinYin(user.getAlias()).toUpperCase(),
                        user.getObjid()));
                checkEnd();
            }
        }
    }

    private String transformPinYin(String character) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < character.length(); i++) {
            buffer.append(Pinyin.toPinyin(character.charAt(i)));
        }
        return buffer.toString();
    }

    synchronized private void checkEnd() {
        count++;
        L.d("checkEnd count="+count+"|length="+length);
        if (count == length) {
            Collections.sort(mContactsList, new Comparator<ContactsItem>() {
                @Override
                public int compare(ContactsItem t, ContactsItem t1) {
                    return t.getPinyin().compareTo(t1.getPinyin());
                }
            });
            updateNavigatorData(mContactsList);
            mIContactsView.loadData(mContactsList);
            mIContactsView.loadNavigatorData(mNaviHeads, mNaviHeadsSet);
        }
    }

    private void updateNavigatorData(List<ContactsItem> list) {
        for (ContactsItem c : list) {
            mNaviHeadsSet.add(c.getPinyin().substring(0,1));
        }
        for (String s : mNaviHeadsSet) {
            mNaviHeads.add(s);
        }
    }

    @Override
    public void loadMoreData() {

    }

    @Inject
    ContactsPresenter(IContactsView view, DaoSession daosession) {
        mIContactsView = view;
        userDao = daosession.getUserDao();
    }

    @Override
    public void getClassMates() {
        String graduclass = BmobUtils.getCurrentUser().getGraduClass();
        L.d("getClassMates:"+graduclass);
        BmobQuery<GraduClass> query = new BmobQuery<GraduClass>();
        query.getObject(graduclass, new QueryListener<GraduClass>() {

            @Override
            public void done(GraduClass aClass, BmobException e) {
                if (aClass != null) {
                    mClassMates = aClass.getClassmates();
                    loadData();
                } else {
                    L.d("GraduClass == null");
                }
            }
        });

    }
}
