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


public class GraduClass extends BmobObject {
    private String admin;
    private String className;
    private String graduYear;
    private String district;
    private ArrayList<String> classmates;
    private String chatRoomTableName;
    private String freshNewsTableName;

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGraduYear() {
        return graduYear;
    }

    public void setGraduYear(String graduYear) {
        this.graduYear = graduYear;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public ArrayList<String> getClassmates() {
        return classmates;
    }

    public void setClassmates(ArrayList<String> classmates) {
        this.classmates = classmates;
    }

    public String getChatRoomTableName() {
        return chatRoomTableName;
    }

    public void setChatRoomTableName(String chatRoomTableName) {
        this.chatRoomTableName = chatRoomTableName;
    }

    public String getFreshNewsTableName() {
        return freshNewsTableName;
    }

    public void setFreshNewsTableName(String freshNewsTableName) {
        this.freshNewsTableName = freshNewsTableName;
    }
}
