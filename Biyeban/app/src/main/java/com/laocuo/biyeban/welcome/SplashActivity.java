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

package com.laocuo.biyeban.welcome;

import android.widget.TextView;

import com.laocuo.biyeban.BiyebanApp;
import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseActivity;
import com.laocuo.biyeban.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;

public class SplashActivity extends BaseActivity {
    @BindView(R.id.wel_txt)
    TextView mTips;

    private boolean mIsSkip = false;
    private int timeleft = 3;
    private TimeRunnable mTimeRunnable = new TimeRunnable();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void doInject() {

    }

    @Override
    protected void doInit() {
        //第一：默认初始化
        Bmob.initialize(this, BiyebanApp.getInstance().getApplicationID());

        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        //BmobConfig config =new BmobConfig.Builder(this)
        ////设置appkey
        //.setApplicationId("Your Application ID")
        ////请求超时时间（单位为秒）：默认15s
        //.setConnectTimeout(30)
        ////文件分片上传时每片的大小（单位字节），默认512*1024
        //.setUploadBlockSize(1024*1024)
        ////文件的过期时间(单位为秒)：默认1800s
        //.setFileExpiration(2500)
        //.build();
        //Bmob.initialize(config);
        updateUI();
    }

    @Override
    protected void getData(boolean isRefresh) {
        mTips.postDelayed(mTimeRunnable, 1000);
    }

    private void updateUI() {
        mTips.setText("SKIP "+timeleft);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @OnClick(R.id.wel_txt)
    public void onClick() {
        _doSkip();
    }

    private class TimeRunnable implements Runnable {

        @Override
        public void run() {
            if (!mIsSkip) {
                timeleft--;
                updateUI();
                if (timeleft == 0) {
                    _doSkip();
                } else {
                    mTips.postDelayed(mTimeRunnable, 1000);
                }
            }
        }
    }

    private void _doSkip() {
        if (!mIsSkip) {
            mIsSkip = true;
            finish();
            Utils.enterMain(SplashActivity.this);
        }
    }
}
