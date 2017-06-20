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

package com.laocuo.biyeban.main.contacts;

import com.laocuo.recycler.entity.MultiItemEntity;


public class ContactsItem extends MultiItemEntity {
    public static final int ITEM_TYPE_NORMAL = 1;
    private String avatar;
    private String alias;
    private String objId;
    private String pinyin;

    public ContactsItem(int itemType,
                        String avatar,
                        String alias,
                        String pinyin,
                        String objId) {
        super(itemType);
        this.avatar = avatar;
        this.alias = alias;
        this.pinyin = pinyin;
        this.objId = objId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAlias() {
        return alias;
    }

    public String getObjId() {
        return objId;
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getFirstPinYin() {
        return pinyin.substring(0,1);
    }
}
