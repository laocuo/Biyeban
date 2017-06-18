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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.bmob.Chat;
import com.laocuo.biyeban.utils.FactoryInterface;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.SnackbarUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

public class ChatRoomFragment extends BaseFragment {
    private static final String TYPE_KEY = "TypeKey";
    private String mTitle;
    private String chatRoomName;

    @BindView(R.id.lv_data)
    ListView lv_data;
    @BindView(R.id.et_content)
    EditText et_content;
    @BindView(R.id.ll_bottom)
    LinearLayout mLayout;

    private MyAdapter myAdapter;
    private List<Chat> messages = new ArrayList<>();
    private BmobRealTimeData data = new BmobRealTimeData();

    private BiyebanUser user;
    private String avatar_url;

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

    }

    @Override
    protected void doInit() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        init();
        lv_data.setDividerHeight(0);
        myAdapter = new MyAdapter();
        lv_data.setAdapter(myAdapter);
        registerForContextMenu(lv_data);
    }

    @Override
    protected void getData(boolean isRefresh) {
        L.d("ChatRoomFragment:getData isRefresh="+isRefresh);
        if (isRefresh == true) {
//            mPresenter.loadMoreData();
            finishRefresh();
        } else {
            chat_init();
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
        String userObjId = messages.get(menuInfo.position).getUserObjectId();
        if (item.getItemId() == R.id.stopchat) {
            stopOrallowChat(false, userObjId);
        }
        if (item.getItemId() == R.id.allowchat) {
            stopOrallowChat(true, userObjId);
        }
        return true;
    }

    private void stopOrallowChat(boolean b, String userObjId) {
        BiyebanUser biyebanUser = new BiyebanUser();
        biyebanUser.setCanChat(b);
        biyebanUser.update(userObjId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e != null) {
                    L.d(e.toString());
                    SnackbarUtil.showShortSnackbar(mLayout, "失败");
                } else {
                    SnackbarUtil.showShortSnackbar(mLayout, "成功");
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        data.unsubTableUpdate(chatRoomName);
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
                BmobQuery<BiyebanUser> query = new BmobQuery<BiyebanUser>();
                query.getObject(user.getObjectId(), new QueryListener<BiyebanUser>() {
                    @Override
                    public void done(BiyebanUser biyebanUser, BmobException e) {
                        if (e == null) {
                            Boolean canChat = biyebanUser.getCanChat();
                            L.d("canChat.booleanValue()="+canChat.booleanValue());
                            if (canChat.booleanValue() == true) {
                                sendMsg(name, content, biyebanUser.getUsername(), biyebanUser.getObjectId());
                                return;
                            }
                        } else {
                            L.d(e.toString());
                        }
                        showProgress(false);
                        SnackbarUtil.showShortSnackbar(mLayout, "禁言");
                    }
                });
            }
        } else {
            SnackbarUtil.showShortSnackbar(mLayout, "请先登录");
        }
    }

    private void init() {
        mProgressDialog = new ProgressDialog(mContext);
        user = BmobUser.getCurrentUser(BiyebanUser.class);
        chatRoomName = user.getGraduClass().getObjectId();
        L.d("chatRoomName = "+chatRoomName);
        BmobFile avatar = user.getAvatar();
        avatar_url = avatar == null ? "" : avatar.getFileUrl();
        data.start(new ValueEventListener() {

            @Override
            public void onDataChange(JSONObject arg0) {
                // TODO Auto-generated method stub
                if(BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0.optString("action"))){
                    JSONObject data = arg0.optJSONObject("data");
                    String name = data.optString("name");
                    String username = data.optString("username");
                    String content = data.optString("content");
                    String avatar = data.optString("avatar");
                    String time = data.optString("time");
//                    L.d("UPDATETABLE:name="+name+" content="+content);
                    Chat chat = new Chat(name, content, username, chatRoomName);
                    if (avatar != null && !TextUtils.isEmpty(avatar)) {
                        chat.setAvatar(avatar);
                    }
                    //use remote time
                    chat.setTime(time);
                    //use local time
//                    chat.setTime(getCurrentTime());
                    messages.add(chat);
                    refrestList();
                }
            }

            @Override
            public void onConnectCompleted(Exception e) {
                // TODO Auto-generated method stub
                if(data.isConnected()){
                    data.subTableUpdate(chatRoomName);
                }
            }
        });
    }

    private void chat_init() {
        showLoading();
        BmobQuery bmobQuery = new BmobQuery(chatRoomName);
        bmobQuery.order("-createdAt");
        bmobQuery.setLimit(20);
        bmobQuery.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray array, BmobException e) {
                if (e == null) {
                    int len = array.length();
                    L.d("array.length()"+len);
                    for (int i=0;i<len;i++) {
                        JSONObject data = null;
                        try {
                            data = array.getJSONObject(len-1-i);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        String name = data.optString("name");
                        String username = data.optString("username");
                        String content = data.optString("content");
                        String avatar = data.optString("avatar");
                        String time = data.optString("time");
                        Chat chat = new Chat(name, content, username, chatRoomName);
                        if (avatar != null && !TextUtils.isEmpty(avatar)) {
                            chat.setAvatar(avatar);
                        }
                        chat.setTime(time);
                        messages.add(chat);
                    }
                    if (messages.size() > 0) {
                        refrestList();
                    }
                } else {
                    L.d(e.toString());
                }
                hideLoading();
            }
        });
//        bmobQuery.findObjects(new FindListener<Chat>() {
//
//            @Override
//            public void done(List<Chat> list, BmobException e) {
//                if (e == null) {
//                    for (int i=0;i<list.size();i++) {
//                        messages.add(list.get(list.size()-1-i));
//                    }
////                    messages.addAll(list);
//                    if (messages.size() > 0) {
//                        refrestList();
//                    }
//                }
//                showProgress(false);
//            }
//        });
    }

    private void refrestList() {
        myAdapter.notifyDataSetChanged();
        lv_data.smoothScrollToPosition(myAdapter.getCount() - 1);
    }

    private String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String time = format.format(new Date(System.currentTimeMillis()));
        return time;
    }

    private void sendMsg(String name, String msg, String username, String objId){
        Chat chat = new Chat(name, msg, username, chatRoomName);
        if (!TextUtils.isEmpty(avatar_url)) {
            chat.setAvatar(avatar_url);
        }
        chat.setUserObjectId(objId);
        chat.setTime(getCurrentTime());
        chat.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                showProgress(false);
                if (e == null) {
                    et_content.setText("");
                } else {
                    SnackbarUtil.showShortSnackbar(mLayout, "发送失败");
                }
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        ViewHolder holder;

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return messages.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return messages.get(position);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            String username = messages.get(position).getUsername();
            if (username != null && user.getUsername().contentEquals(username)) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if(convertView == null){
                if (getItemViewType(position) == 0) {
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.chat_list_item_send, null);
                } else {
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.chat_list_item_recv, null);
                }
                holder = new ViewHolder();

                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_avatar = (ImageView) convertView.findViewById(R.id.tv_avatar);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            Chat chat = messages.get(position);
            if (chat.getUsername().equalsIgnoreCase("admin")) {
                StringBuilder display_name = new StringBuilder(chat.getName());
                display_name.append("(").append(chat.getUsername()).append(")");
                holder.tv_name.setText(display_name);
            } else {
                holder.tv_name.setText(chat.getName());
            }
            holder.tv_content.setText(chat.getContent());
            String time = chat.getTime();
            if (time != null && !TextUtils.isEmpty(time)) {
                if (position > 0 &&
                        chat.getTime() != null &&
                        chat.getTime().equals(messages.get(position - 1).getTime())) {
                    holder.tv_time.setVisibility(View.GONE);
                } else {
                    holder.tv_time.setText(chat.getTime());
                    holder.tv_time.setVisibility(View.VISIBLE);
                }
            } else {
                holder.tv_time.setVisibility(View.GONE);
            }
            String avatar = chat.getAvatar();
            if (avatar != null && !TextUtils.isEmpty(avatar)) {
                FactoryInterface.setAvatar(getActivity(), chat.getAvatar(), holder.tv_avatar);
            } else {
                holder.tv_avatar.setImageResource(R.drawable.user);
            }

            return convertView;
        }

        class ViewHolder{
            TextView tv_name;
            TextView tv_content;
            TextView tv_time;
            ImageView tv_avatar;
        }

    }
}
