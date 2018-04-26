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
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

public class ChatRoomPresenter implements IChatRoomPresenter {
    public static final int STEP  = 10;
    private IChatRoomView mIChatRoomView;
    private BmobRealTimeData data = new BmobRealTimeData();
    private BiyebanUser user;
    private String chatRoomTableName;
    private List<ChatItem> messages = new ArrayList<>();
    private List<ChatItem> moreMessages = new ArrayList<>();
    private boolean isListenTable = false;
    private ChatValueEventListener mChatValueEventListener = null;
    private int skip = 0;
    private String mCurrentUserObjId;

    @Override
    public void loadData() {
        mIChatRoomView.showLoading();
        messages.clear();
        BmobQuery bmobQuery = new BmobQuery(chatRoomTableName);
        bmobQuery.order("-createdAt");
        bmobQuery.setLimit(STEP);
        bmobQuery.setSkip(skip * STEP);
        skip++;
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
                            String objId = data.optString("userObjectId");
                            int itemType;
                            if (objId != null && objId.equals(mCurrentUserObjId)) {
                                itemType = ChatItem.ITEM_TYPE_SEND;
                            } else {
                                itemType = ChatItem.ITEM_TYPE_RECV;
                            }
                            ChatItem chat = new ChatItem(itemType,
                                    objId,
                                    data.optString("content"),
                                    data.optString("time"));
                            messages.add(chat);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (messages.size() > 0) {
                        mIChatRoomView.loadData(messages);
                    } else {
                        mIChatRoomView.loadNoData();
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
        moreMessages.clear();
        BmobQuery bmobQuery = new BmobQuery(chatRoomTableName);
        bmobQuery.order("-createdAt");
        bmobQuery.setLimit(STEP);
        bmobQuery.setSkip(skip * STEP);
        skip++;
        bmobQuery.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray array, BmobException e) {
                if (e == null) {
                    int len = array.length();
                    L.d("array.length()"+len);
                    for (int i=0;i<len;i++) {
                        JSONObject data;
                        try {
                            data = array.getJSONObject(i);
                            String objId = data.optString("userObjectId");
                            int itemType;
                            if (objId != null && objId.equals(mCurrentUserObjId)) {
                                itemType = ChatItem.ITEM_TYPE_SEND;
                            } else {
                                itemType = ChatItem.ITEM_TYPE_RECV;
                            }
                            ChatItem chat = new ChatItem(itemType,
                                    objId,
                                    data.optString("content"),
                                    data.optString("time"));
                            moreMessages.add(chat);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (moreMessages.size() > 0) {
                        mIChatRoomView.loadMoreData(moreMessages);
                    }
                } else {
                    L.d(e.toString());
                }
            }
        });
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
    }

    @Override
    public void listenTable() {
        if (!isListenTable && data != null) {
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
        if (isListenTable && data != null) {
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

    private void sendMsg(String userObjId, String content, String chatRoomTableName) {
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
    }

    @Override
    public void sendMsg(final String userObjId, final String content) {
        if (true) {
            sendMsg(userObjId, content, chatRoomTableName);
        } else {
            BmobQuery<BiyebanUser> query = new BmobQuery<BiyebanUser>();
            query.getObject(userObjId, new QueryListener<BiyebanUser>() {
                @Override
                public void done(BiyebanUser biyebanUser, BmobException e) {
                    if (e == null) {
                        Boolean canChat = biyebanUser.getCanChat();
                        L.d("canChat.booleanValue()=" + canChat.booleanValue());
                        if (canChat.booleanValue() == true) {
                            sendMsg(userObjId, content, chatRoomTableName);
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
    }

    @Override
    public void reload() {
        updateUserInfo();
        loadData();
    }

    public void setCurrentUserObjId(String currentUserObjId) {
        mCurrentUserObjId = currentUserObjId;
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
                int itemType;
                if (objId != null && objId.equals(mCurrentUserObjId)) {
                    itemType = ChatItem.ITEM_TYPE_SEND;
                } else {
                    itemType = ChatItem.ITEM_TYPE_RECV;
                }
                ChatItem chat = new ChatItem(itemType, objId, content, time);
                //use remote time
//                chat.setTime(time);
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
