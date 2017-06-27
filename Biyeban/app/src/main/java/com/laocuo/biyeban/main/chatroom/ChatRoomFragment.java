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
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.bmob.Chat;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.SnackbarUtil;
import com.laocuo.biyeban.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;


public class ChatRoomFragment extends BaseFragment<ChatRoomPresenter>
    implements IChatRoomView {
    private static final String TYPE_KEY = "TypeKey";
    private String mTitle;
    private BiyebanUser user = Utils.getCurrentUser();

    @BindView(R.id.lv_data)
    ListView lv_data;
    @BindView(R.id.et_content)
    EditText et_content;
    @BindView(R.id.ll_bottom)
    LinearLayout mLayout;

    @Inject
    ChatListAdapter mChatListAdapter;

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
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mProgressDialog = new ProgressDialog(mContext);
        lv_data.setDividerHeight(0);
        registerForContextMenu(lv_data);
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
        String userObjId = mChatListAdapter.getChatList().get(menuInfo.position).getUserObjectId();
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
            final String name = TextUtils.isEmpty(user.getAlias()) ? user.getUsername():user.getAlias();
            final String content = et_content.getText().toString();
            if (TextUtils.isEmpty(content)) {
                SnackbarUtil.showShortSnackbar(mLayout, "内容不能为空");
                return;
            } else {
                showProgress(true);
                mPresenter.sendMsg(name, content);
            }
        } else {
            SnackbarUtil.showShortSnackbar(mLayout, "请先登录");
        }
    }

    private void refrestList() {
        mChatListAdapter.notifyDataSetChanged();
        lv_data.smoothScrollToPosition(mChatListAdapter.getCount() - 1);
    }

    @Override
    public void recvMessage(Chat chat) {
        mChatListAdapter.addChat(chat);
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
    public void loadData(List<Chat> data) {
        mChatListAdapter.setChatList(data);
        lv_data.setAdapter(mChatListAdapter);
        refrestList();
        mPresenter.listenTable();
    }

    @Override
    public void loadMoreData(List<Chat> data) {

    }

    @Override
    public void loadNoData() {

    }
}
