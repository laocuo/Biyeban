package com.laocuo.biyeban;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.laocuo.biyeban.greendao.DaoMaster;
import com.laocuo.biyeban.greendao.DaoSession;

import cn.bmob.v3.Bmob;

/**
 * Created by hoperun on 5/27/17.
 */

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
        setDatabase();
    }

    public String getApplicationID() {
        return ApplicationID;
    }

    public static BiyebanApp getInstance() {
        if (instance == null) {
            instance = new BiyebanApp();
        }
        return instance;
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