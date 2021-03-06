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

package com.laocuo.biyeban;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.laocuo.biyeban.greendao.DaoMaster;
import com.laocuo.biyeban.greendao.DaoSession;


public class BiyebanApp extends Application {
    private static BiyebanApp instance;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase mDatabase;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private String ApplicationID = "b623f2f510d079549ae09e7031664748";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        setDatabase();
    }

    public String getApplicationID() {
        return ApplicationID;
    }

    public static BiyebanApp getInstance() {
        return instance;
    }

    public int getUploadImgsMax() {
        return 3;
    }

    private void setDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(this, "biyeban", null);
        mDatabase = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDatabase);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoMaster getDaoMaster() {
        return mDaoMaster;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}