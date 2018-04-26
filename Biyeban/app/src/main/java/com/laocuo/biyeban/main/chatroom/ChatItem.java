package com.laocuo.biyeban.main.chatroom;

import com.laocuo.recycler.entity.MultiItemEntity;


public class ChatItem extends MultiItemEntity {
    public static final int ITEM_TYPE_SEND = 1;
    public static final int ITEM_TYPE_RECV = 2;

    private String userObjectId;
    private String content;
    private String time;

    public ChatItem(int itemType) {
        super(itemType);
    }

    public ChatItem(int itemType, String userObjectId, String content, String time) {
        super(itemType);
        this.setUserObjectId(userObjectId);
        this.setContent(content);
        this.setTime(time);
    }

    public String getUserObjectId() {
        return userObjectId;
    }

    public void setUserObjectId(String userObjectId) {
        this.userObjectId = userObjectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
