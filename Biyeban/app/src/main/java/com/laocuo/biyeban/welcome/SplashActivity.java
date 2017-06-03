package com.laocuo.biyeban.welcome;

import android.content.Intent;
import android.widget.TextView;

import com.laocuo.biyeban.BiyebanApp;
import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseActivity;
import com.laocuo.biyeban.login.BiyebanUser;
import com.laocuo.biyeban.login.LoginActivity;
import com.laocuo.biyeban.main.MainActivity;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

/**
 * Created by hoperun on 6/1/17.
 */

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
    }

    @Override
    protected void doLoadData() {
        mTips.postDelayed(mTimeRunnable, 1000);
    }

    @Override
    public void updateUI() {
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
            final BiyebanUser user = BmobUser.getCurrentUser(BiyebanUser.class);
            if (user != null) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
//            overridePendingTransition(R.anim.hold, R.anim.zoom_in_exit);
        }
    }
}