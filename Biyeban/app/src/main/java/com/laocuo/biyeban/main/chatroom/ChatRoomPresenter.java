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


import android.text.TextUtils;

import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.bmob.Chat;
import com.laocuo.biyeban.utils.BmobUtils;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

public class ChatRoomPresenter implements IChatRoomPresenter {
    private IChatRoomView mIChatRoomView;
    private BmobRealTimeData data = new BmobRealTimeData();
    private BiyebanUser user;
    private String chatRoomTableName;
    private String avatar_url;
    private List<Chat> messages = new ArrayList<>();
    private boolean isListenTable = false;
    private ChatValueEventListener mChatValueEventListener = null;

    @Override
    public void loadData() {
        mIChatRoomView.showLoading();
        messages.clear();
        BmobQuery bmobQuery = new BmobQuery(chatRoomTableName);
        bmobQuery.order("-createdAt");
        bmobQuery.setLimit(20);
        bmobQuery.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray array, BmobException e) {
                if (e == null) {
                    int len = array.length();
                    L.d("array.length()"+len);
                    for (int i=0;i<len;i++) {
                        JSONObject data;
                        try {
                            data = array.getJSONObject(len-1-i);
                            Chat chat = new Chat(data.optString("userObjectId"),
                                    data.optString("content"),
                                    chatRoomTableName);
                            chat.setTime(data.optString("time"));
                            messages.add(chat);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (messages.size() > 0) {
                        mIChatRoomView.loadData(messages);
                    }
                } else {
                    L.d(e.toString());
                }
                mIChatRoomView.hideLoading();
            }
        });
    }

    @Override
    public void loadMoreData() {

    }

    @Inject
    ChatRoomPresenter(IChatRoomView view) {
        mIChatRoomView = view;
        updateUserInfo();
    }

    private void updateUserInfo() {
        user = BmobUtils.getCurrentUser();
        chatRoomTableName = Utils.CHATROOM + user.getGraduClass();
        L.d("updateUserInfo chatRoomName = "+chatRoomTableName);
        BmobFile avatar = user.getAvatar();
        avatar_url = avatar == null ? "" : avatar.getFileUrl();
    }

    @Override
    public void listenTable() {
        if (!isListenTable) {
            isListenTable = true;
            L.d("listenTable = "+chatRoomTableName);
            if (mChatValueEventListener == null) {
                mChatValueEventListener = new ChatValueEventListener();
            }
            data.start(mChatValueEventListener);
        }
    }

    @Override
    public void unlistenTable() {
        if (isListenTable) {
            L.d("unlistenTable = "+chatRoomTableName);
            isListenTable = false;
            data.unsubTableUpdate(chatRoomTableName);
        }
    }

    @Override
    public void stopOrallowChat(boolean b, String userObjId) {
        BiyebanUser biyebanUser = new BiyebanUser();
        biyebanUser.setCanChat(b);
        biyebanUser.update(userObjId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e != null) {
                    L.d(e.toString());
                    mIChatRoomView.stopOrallowChatResult(false);
                } else {
                    mIChatRoomView.stopOrallowChatResult(true);
                }
            }
        });
    }

    @Override
    public void sendMsg(final String userObjId, final String content) {
        BmobQuery<BiyebanUser> query = new BmobQuery<BiyebanUser>();
        query.getObject(userObjId, new QueryListener<BiyebanUser>() {
            @Override
            public void done(BiyebanUser biyebanUser, BmobException e) {
                if (e == null) {
                    Boolean canChat = biyebanUser.getCanChat();
                    L.d("canChat.booleanValue()="+canChat.booleanValue());
                    if (canChat.booleanValue() == true) {
                        Chat chat = new Chat(userObjId, content, chatRoomTableName);
                        chat.setTime(Utils.getCurrentTime());
                        chat.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    mIChatRoomView.sendMsg(true);
                                } else {
                                    L.d(e.toString());
                                    mIChatRoomView.sendMsg(false);
                                }
                            }
                        });
                        return;
                    } else {
                        mIChatRoomView.chatForbidden();
                    }
                } else {
                    L.d(e.toString());
                }
            }
        });
    }

    @Override
    public void reload() {
        updateUserInfo();
        loadData();
    }

    private class ChatValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(JSONObject arg0) {
            // TODO Auto-generated method stub
            if(BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0.optString("action"))){
                JSONObject data = arg0.optJSONObject("data");
                String objId = data.optString("userObjectId");
                String content = data.optString("content");
                String time = data.optString("time");
//                    L.d("UPDATETABLE:name="+name+" content="+content);
                Chat chat = new Chat(objId, content, chatRoomTableName);
                //use remote time
                chat.setTime(time);
                //use local time
//                    chat.setTime(getCurrentTime());
                mIChatRoomView.recvMessage(chat);
            }
        }

        @Override
        public void onConnectCompleted(Exception e) {
            // TODO Auto-generated method stub
            if(data.isConnected()){
                isListenTable = true;
                data.subTableUpdate(chatRoomTableName);
            }
        }
    }
}
