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

package com.laocuo.biyeban.bmob;

import cn.bmob.v3.BmobObject;

public class Chat extends BmobObject {
    private String name;
    private String username;
    private String content;
    private String avatar;
    private String time;
    private String userObjectId;

    public Chat(String userObjectId, String tablename){
        this.setName("Administrator");
        this.setContent("Welcome!");
        this.setAvatar("");
        this.setTime("00:00");
        this.setUsername("");
        this.setTableName(tablename);
        this.setUserObjectId(userObjectId);
    }

    public Chat(String name, String content, String username, String tablename){
        this.name = name;
        this.content = content;
        this.username = username;
        this.setTableName(tablename);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getUserObjectId() {
        return userObjectId;
    }
    public void setUserObjectId(String userObjectId) {
        this.userObjectId = userObjectId;
    }
}
