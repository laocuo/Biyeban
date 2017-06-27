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

package com.laocuo.biyeban.publish.news;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.bmob.FreshNews;
import com.laocuo.biyeban.publish.IPublishInterface;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.SnackbarUtil;
import com.laocuo.biyeban.utils.Utils;

import butterknife.BindView;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class PublishNewsFragment extends BaseFragment implements View.OnClickListener {
    private IPublishInterface mIPublishInterface;
    private ActionBar mActionBar;
    private BiyebanUser user = Utils.getCurrentUser();
    private String freshNewsTableName;

    @BindView(R.id.container)
    LinearLayout mLinearLayout;
    @BindView(R.id.content)
    EditText mContent;

    public static PublishNewsFragment newInstance(IPublishInterface anInterface) {
        PublishNewsFragment fragment = new PublishNewsFragment();
        fragment.setIPublishInterface(anInterface);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_publishnews;
    }

    @Override
    protected void doInject() {

    }

    @Override
    protected void doInit() {
        mProgressDialog = new ProgressDialog(mContext);
        freshNewsTableName = user.getGraduClass().getObjectId() + Utils.FRESHNEWS;
        setHasOptionsMenu(true);
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setCustomView(R.layout.send_button);
        mActionBar.getCustomView().findViewById(R.id.btn_send).setOnClickListener(this);
    }

    @Override
    protected void getData(boolean isRefresh) {

    }

    public void setIPublishInterface(IPublishInterface i) {
        mIPublishInterface = i;
    }

    @Override
    public void onClick(View v) {
        L.d("PublishNewsFragment Send");
        String content = mContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            SnackbarUtil.showShortSnackbar(mLinearLayout, "内容不能为空");
        } else {
            showProgress(true);
            FreshNews freshNews = new FreshNews(user.getObjectId(), freshNewsTableName);
            freshNews.setContent(content);
            freshNews.setTime(Utils.getCurrentTime());
            freshNews.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        SnackbarUtil.showShortSnackbar(mLinearLayout, "发送成功");
                        mIPublishInterface.exit();
                    } else {
                        showProgress(false);
                        L.d(e.toString());
                        SnackbarUtil.showShortSnackbar(mLinearLayout, "发送失败");
                    }
                }
            });
        }
    }
}
