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

package com.laocuo.biyeban.utils;


import android.text.TextUtils;

import com.laocuo.biyeban.bmob.BiyebanUser;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class BmobUtils {
    public static BiyebanUser getCurrentUser() {
        final BiyebanUser user = BmobUser.getCurrentUser(BiyebanUser.class);
        return user;
    }

    public static void logOut() {
        final BiyebanUser user = BmobUser.getCurrentUser(BiyebanUser.class);
        if (user != null) {
            user.logOut();
        }
    }

    public static void deleteBmobFile(String url) {
        if (!TextUtils.isEmpty(url)) {
            BmobFile bmobFile = new BmobFile();
            bmobFile.setUrl(url);
            bmobFile.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null) {
                        L.i("bmob", "文件删除失败：" + e.getMessage() + "," + e.getErrorCode());
                    } else {
                        L.d("bmob", "文件删除成功");
                    }
                }
            });
        }
    }

    public static void uploadBmobFile(final File file, final UploadBmobFileListener l) {
        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    L.i("bmob", "文件上传成功，返回的名称--" + bmobFile.getFileUrl());
                    l.success(bmobFile);
                } else {
                    L.d("bmob", "文件上传失败：" + e.getMessage() + "," + e.getErrorCode());
                    l.fail();
                }
            }
        });
    }
}
