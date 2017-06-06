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

package com.laocuo.biyeban.test;

import android.util.Log;

import com.laocuo.biyeban.base.IBaseView;
import com.laocuo.biyeban.greendao.DaoSession;
import com.laocuo.biyeban.greendao.User;
import com.laocuo.biyeban.greendao.UserDao;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by hoperun on 5/26/17.
 */

public class TestPresenter implements TestContact.Presentr {
    private IBaseView mView;
    private DaoSession mSession;
    private UserDao mSessionUserDao;
    private long index = 1;
    @Override
    public void loadData() {
        //do something
        Log.d("xxxx","loadData");

        mView.updateUI();
    }

    @Inject
    TestPresenter(IBaseView view, DaoSession daoSession) {
        mView = view;
        mSession = daoSession;
        mSessionUserDao = mSession.getUserDao();
    }

    private void add() {
        mSessionUserDao.insert(new User(index, "one"));
        index++;
    }

    private String query() {
        List<User> userList = mSessionUserDao.loadAll();
        String username = "";
        for (int i=0; i<userList.size();i++) {
            username += userList.get(i).getId();
            username += " ";
            username += userList.get(i).getName();
            username += " ";
        }
        return username;
    }
}
