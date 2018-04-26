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

package com.laocuo.biyeban.main.chatroom;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.utils.BmobUtils;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.SnackbarUtil;
import com.laocuo.recycler.helper.RecyclerViewHelper;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;


public class ChatRoomFragment extends BaseFragment<ChatRoomPresenter>
    implements IChatRoomView {
    private static final String TYPE_KEY = "TypeKey";
    private String mTitle;
    private BiyebanUser user;
    private boolean isEnd = true;

    private LinearLayoutManager layoutManager;

    @BindView(R.id.lv_data)
    RecyclerView lv_data;
    @BindView(R.id.et_content)
    EditText et_content;
    @BindView(R.id.ll_bottom)
    LinearLayout mLayout;

    @Inject
    ChatListAdapter mAdapter;

    public static ChatRoomFragment newInstance() {
        ChatRoomFragment fragment = new ChatRoomFragment();
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
    public void onPause() {
        super.onPause();
        mPresenter.unlistenTable();
    }

    @Override
    public void onResume() {
        super.onResume();
        String username = user.getUsername();
        String currentusername = BmobUtils.getCurrentUser().getUsername();
        mPresenter.setCurrentUserObjId(user.getObjectId());
        if (!username.equals(currentusername)) {
            user = BmobUtils.getCurrentUser();
            mPresenter.setCurrentUserObjId(user.getObjectId());
//            mAdapter.setCurrentUserObjId(user.getObjectId());
            mPresenter.reload();
        }
        mPresenter.listenTable();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chatroom;
    }

    @Override
    protected void doInject() {
        DaggerChatRoomComponent.builder()
                .chatRoomModule(new ChatRoomModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void doInit() {
        user = BmobUtils.getCurrentUser();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mProgressDialog = new ProgressDialog(mContext);
        registerForContextMenu(lv_data);
        RecyclerViewHelper.initRecyclerViewV(mContext, lv_data, false, mAdapter);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        lv_data.setLayoutManager(layoutManager);
        lv_data.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (layoutManager.findFirstVisibleItemPosition() == 0 && isEnd == false) {
                        L.d("mPresenter.loadMoreData");
                        mPresenter.loadMoreData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void getData(boolean isRefresh) {
        L.d("ChatRoomFragment:getData isRefresh="+isRefresh);
        if (isRefresh == true) {
//            mPresenter.loadMoreData();
            finishRefresh();
        } else {
            mPresenter.loadData();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (user.getUsername().contentEquals("admin")) {
            getActivity().getMenuInflater().inflate(R.menu.chat_context_menu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        return super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String userObjId = ((ChatItem)mAdapter.getData().get(menuInfo.position)).getUserObjectId();
        if (item.getItemId() == R.id.stopchat) {
            mPresenter.stopOrallowChat(false, userObjId);
        }
        if (item.getItemId() == R.id.allowchat) {
            mPresenter.stopOrallowChat(true, userObjId);
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unlistenTable();
    }

    @OnClick(R.id.btn_send)
    void onSendClick(View v) {
        // TODO Auto-generated method stub
        if (user != null) {
            final String content = et_content.getText().toString();
            if (TextUtils.isEmpty(content)) {
                SnackbarUtil.showShortSnackbar(mLayout, "内容不能为空");
                return;
            } else {
                showProgress(true);
                mPresenter.sendMsg(user.getObjectId(), content);
            }
        } else {
            SnackbarUtil.showShortSnackbar(mLayout, "请先登录");
        }
    }

    private void refrestList() {
        mAdapter.notifyDataSetChanged();
        lv_data.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void recvMessage(ChatItem chat) {
        mAdapter.addLastItem(chat);
        refrestList();
    }

    @Override
    public void stopOrallowChatResult(boolean b) {
        if (b == true) {
            SnackbarUtil.showShortSnackbar(mLayout, "成功");
        } else {
            SnackbarUtil.showShortSnackbar(mLayout, "失败");
        }
    }

    @Override
    public void sendMsg(boolean b) {
        showProgress(false);
        if (b == true) {
            et_content.setText("");
        } else {
            SnackbarUtil.showShortSnackbar(mLayout, "发送失败");
        }
    }

    @Override
    public void chatForbidden() {
        showProgress(false);
        SnackbarUtil.showShortSnackbar(mLayout, "禁言");
    }

    @Override
    public void loadData(List<ChatItem> data) {
        L.d("loadData size="+data.size());
        checkEnd(data.size());
        mAdapter.updateItems(data);
    }

    @Override
    public void loadMoreData(List<ChatItem> data) {
        checkEnd(data.size());
        L.d("loadMoreData size="+data.size());
        mAdapter.addItems(data, 0);
        layoutManager.scrollToPosition(data.size());
    }

    @Override
    public void loadNoData() {

    }

    private void checkEnd(int size) {
        if (size < ChatRoomPresenter.STEP) {
            isEnd = true;
        } else {
            isEnd = false;
        }
    }
}
