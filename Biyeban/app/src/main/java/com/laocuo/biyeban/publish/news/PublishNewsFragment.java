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
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.laocuo.biyeban.BiyebanApp;
import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.publish.IPublishInterface;
import com.laocuo.biyeban.utils.BmobUtils;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.SnackbarUtil;
import com.laocuo.biyeban.utils.Utils;
import com.laocuo.recycler.helper.RecyclerViewHelper;
import com.laocuo.recycler.listener.OnRecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;


public class PublishNewsFragment extends BaseFragment<PublishNewsPresenter>
        implements View.OnClickListener, IPublishNewsInterface, OnRecyclerViewItemClickListener {
    private static final int SELECT_IMAGE = 1;
    private IPublishInterface mIPublishInterface;
    private ActionBar mActionBar;
    private BiyebanUser user = BmobUtils.getCurrentUser();
    private String freshNewsTableName;

    @Inject
    PublishImageListAdapter mAdapter;

    @BindView(R.id.container)
    FrameLayout mLinearLayout;
    @BindView(R.id.content)
    EditText mContent;
    @BindView(R.id.images)
    RecyclerView mRecyclerView;

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
        DaggerPublishNewsComponent.builder()
                .publishNewsModule(new PublishNewsModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void doInit() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mProgressDialog = new ProgressDialog(mContext);
        freshNewsTableName = Utils.FRESHNEWS + user.getGraduClass();
        setHasOptionsMenu(true);
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setCustomView(R.layout.send_button);
        mActionBar.getCustomView().findViewById(R.id.btn_send).setOnClickListener(this);
        RecyclerViewHelper.initRecyclerViewH(mContext, mRecyclerView, false, mAdapter);
        mAdapter.setOnItemClickListener(this);
        mPresenter.setParam(user.getObjectId(), freshNewsTableName);
    }

    @Override
    protected void getData(boolean isRefresh) {
        mAdapter.addLastItem(new ImageItem(ImageItem.ITEM_TYPE_NORMAL));
    }

    public void setIPublishInterface(IPublishInterface i) {
        mIPublishInterface = i;
    }

    @Override
    public void onClick(View v) {
        L.d("PublishNewsFragment Send");
        String content = mContent.getText().toString();
        if (TextUtils.isEmpty(content) && mAdapter.getData().size() < 2) {
            SnackbarUtil.showShortSnackbar(mLinearLayout, "内容不能为空");
        } else {
            showProgress(true);
            List<ImageItem> mImageItemList = mAdapter.getData();
            List<String> mImagesPath = null;
            if (mImageItemList.size() > 1) {
                mImagesPath = new ArrayList<>();
                for (ImageItem item : mImageItemList) {
                    if (!TextUtils.isEmpty(item.getImgPath())) {
                        mImagesPath.add(item.getImgPath());
                    }
                }
            }
            mPresenter.publish(content, mImagesPath);
        }
    }

    private int mSelectPosition;

    @Override
    public void onItemClick(View view, int position) {
        mSelectPosition = position;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
//        intent.putExtra("position", position);
        startActivityForResult(intent, SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_IMAGE:
                if (data != null) {
//                    int pos = data.getIntExtra("position", 0);
                    int pos = mSelectPosition;
                    Uri uri = data.getData();
                    L.d("SELECT_IMAGE: URI = " + uri.toString());
                    List<ImageItem> mImageItemList = mAdapter.getData();
                    boolean isLast = mImageItemList.size() == pos + 1;
                    mImageItemList.get(pos).setImgUrl(uri.toString());
                    mImageItemList.get(pos).setImgPath(Utils.getImageAbsolutePath(getContext(), uri));
                    if (isLast == true && pos < BiyebanApp.getInstance().getUploadImgsMax() - 1) {
                        mAdapter.addLastItem(new ImageItem(ImageItem.ITEM_TYPE_NORMAL));
                    }
                    mAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void publishResult(boolean ret) {
        if (ret == true) {
            SnackbarUtil.showShortSnackbar(mLinearLayout, "发送成功");
            mIPublishInterface.exit();
        } else {
            showProgress(false);
            SnackbarUtil.showShortSnackbar(mLinearLayout, "发送失败");
        }
    }
}
