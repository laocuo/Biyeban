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

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;


public class FreshNews extends BmobObject {
    private String userObjectId;
    private String content;
    private String time;
    private ArrayList<String> pics;
    private ArrayList<String> comments;

    /*
    * update freashnews
    */
    public FreshNews(String tablename) {
        this.setTableName(tablename);
    }

    /*
    * when create a new class, we need to add first freshnews.
    */
    public FreshNews(String userObjectId, String tablename) {
        this.setContent("Welcome!");
        this.setTime("1月1日");
        this.setTableName(tablename);
        this.setUserObjectId(userObjectId);
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

    public ArrayList<String> getPics() {
        return pics;
    }

    public void setPics(ArrayList<String> pics) {
        this.pics = pics;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }
}
