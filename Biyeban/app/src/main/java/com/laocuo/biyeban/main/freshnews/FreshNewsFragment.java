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

package com.laocuo.biyeban.main.freshnews;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.publish.PublishActivity;
import com.laocuo.biyeban.utils.DensityUtil;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.SnackbarUtil;
import com.laocuo.recycler.helper.RecyclerViewHelper;
import com.laocuo.recycler.listener.OnRequestDataListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;


public class FreshNewsFragment extends BaseFragment<FreshNewsPresenter>
        implements IFreshNewsView, OnRequestDataListener{
    private static final String TYPE_KEY = "TypeKey";
    private String mTitle;
    private PopupWindow mAddComment;
    private View.OnClickListener mCommentClickListener;
    private FreshNewsItem mCurrentFreshNewsItem;
    private Dialog mInputDialog;
    private Handler mHandler;
    private int mCommentY;
    private EditText mCommentEditText;
    private Button mCommentSend;

    @Inject
    FreshNewsListAdapter mAdapter;

    @BindView(R.id.freshnews_container)
    FrameLayout mFrameLayout;

    @BindView(R.id.freshnews_list)
    RecyclerView mRecyclerView;

    public static FreshNewsFragment newInstance() {
        FreshNewsFragment fragment = new FreshNewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(TYPE_KEY);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_freshnews;
    }

    @Override
    protected void doInject() {
        DaggerFreshNewsComponent.builder()
                .freshNewsModule(new FreshNewsModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void doInit() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.freshnews_popup_addcomment, null);
        mAddComment = new PopupWindow(v,
                DensityUtil.dip2px(mContext, 120),
                DensityUtil.dip2px(mContext, 30),
                true);
        mAddComment.setOutsideTouchable(false);
        mAddComment.setAnimationStyle(R.style.anim_add_comment);
        mCommentClickListener = new CommentClickListener();
        TextView zan = (TextView) v.findViewById(R.id.zan);
        TextView pinglun = (TextView) v.findViewById(R.id.pinglun);
        zan.setOnClickListener(mCommentClickListener);
        pinglun.setOnClickListener(mCommentClickListener);
        RecyclerViewHelper.initRecyclerViewV(mContext, mRecyclerView, false, mAdapter, this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                showEditComment(false);
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        mInputDialog = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
        mInputDialog.setContentView(R.layout.dialog_input);
        mCommentEditText = mInputDialog.findViewById(R.id.et_content);
        mCommentSend = mInputDialog.findViewById(R.id.btn_send);
        mCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });
        mHandler = new Handler();
    }

    @Override
    protected void getData(boolean isRefresh) {
        if (isRefresh == true) {
            mPresenter.swipeRefresh();
        } else {
            mPresenter.loadData();
        }
    }

    @Override
    public void loadData(List<FreshNewsItem> data) {
        L.d("loadData size="+data.size());
        mAdapter.updateItems(data);
        finishRefresh();
        checkEnd(data.size());
    }

    @Override
    public void loadMoreData(List<FreshNewsItem> data) {
        L.d("loadMoreData size="+data.size());
        mAdapter.addItems(data);
        finishRefresh();
        checkEnd(data.size());
    }

    @Override
    public void loadNoData() {
        L.d("loadNoData");
    }

    @Override
    public void onLoadMore() {
        mPresenter.loadMoreData();
    }

    private void checkEnd(int size) {
        if (size < FreshNewsPresenter.STEP) {
            mAdapter.noMoreData();
        }
    }

    @Override
    public void addCommentClick(View v, FreshNewsItem item) {
        if (mAddComment != null) {
            if (mAddComment.isShowing()) {
                mAddComment.dismiss();
            } else {
                mCommentY = getY(v) + v.getHeight();
                mCurrentFreshNewsItem = item;
                mAddComment.showAsDropDown(v,
                        -1 * (mAddComment.getWidth() + DensityUtil.dip2px(mContext, 10)),
                        (mAddComment.getHeight() - v.getHeight())/2 - mAddComment.getHeight());
            }
        }
    }

    private class CommentClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.zan:
                    showEditComment(false);
                    mAddComment.dismiss();
                    break;
                case R.id.pinglun:
                    showEditComment(true);
                    mAddComment.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    private void addComment() {
        String content = mCommentEditText.getText().toString();
        L.d("addComment:"+content);
        if (!content.isEmpty()) {
            showEditComment(false);
            mPresenter.addComment(content, mCurrentFreshNewsItem);
        } else {
            SnackbarUtil.showShortSnackbar(mFrameLayout, "内容不能为空");
        }
    }

    @Override
    public void addCommentClickResult(boolean ret) {
        mCommentEditText.setText("");
        if (ret == true) {
            mAdapter.notifyDataSetChanged();
        } else {
            SnackbarUtil.showShortSnackbar(mFrameLayout, "发送失败");
        }
    }

    private void showEditComment(boolean b) {
        if (b == true) {
            if (!mInputDialog.isShowing()) {
                mInputDialog.show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int dialogY = getY(mInputDialog.findViewById(R.id.ll_bottom));
                        mRecyclerView.scrollBy(0, mCommentY - dialogY);
                    }
                }, 300);
            }
        } else {
            if (mInputDialog.isShowing()) {
                mInputDialog.dismiss();
            }
        }
    }

    private int getY(View v) {
        int[] rect = new int[2];
        v.getLocationOnScreen(rect);
        return rect[1];
    }

    @Override
    public void floatingClick() {
        PublishActivity.launch(mContext);
    }
}
