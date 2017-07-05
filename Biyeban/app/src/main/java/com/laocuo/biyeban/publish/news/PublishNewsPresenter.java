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

package com.laocuo.biyeban.publish.news;


import android.text.TextUtils;

import com.laocuo.biyeban.bmob.FreshNews;
import com.laocuo.biyeban.utils.BmobUtils;
import com.laocuo.biyeban.utils.L;
import com.laocuo.biyeban.utils.UploadBmobFilesListener;
import com.laocuo.biyeban.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class PublishNewsPresenter implements IPublishNewsPresenter {
    private IPublishNewsInterface mIPublishNewsInterface;
    private String userObjId, freshNewsTableName;

    @Override
    public void loadData() {

    }

    @Override
    public void loadMoreData() {

    }

    @Inject
    PublishNewsPresenter(IPublishNewsInterface view) {
        mIPublishNewsInterface = view;
    }

    @Override
    public void setParam(String userObjId, String freshNewsTableName) {
        this.userObjId = userObjId;
        this.freshNewsTableName = freshNewsTableName;
    }

    @Override
    public void publish(final String content, List<String> mImagesPath) {
        if (mImagesPath != null) {
            BmobUtils.uploadBmobFiles(mImagesPath, new UploadBmobFilesListener() {
                @Override
                public void success(List<String> files) {
                    ArrayList<String> mFiles = new ArrayList<>();
                    for (String file : files) {
                        mFiles.add(file);
                    }
                    submit(content, mFiles);
                }

                @Override
                public void fail() {
                    mIPublishNewsInterface.publishResult(false);
                }
            });
        } else {
            submit(content, null);
        }
    }

    private void submit(String content, ArrayList<String> pics) {
        FreshNews freshNews = new FreshNews(userObjId, freshNewsTableName);
        freshNews.setContent(content);
        freshNews.setTime(Utils.getCurrentDate());
        if (pics != null && pics.size() > 0) {
            freshNews.setPics(pics);
        }
        freshNews.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    mIPublishNewsInterface.publishResult(true);
                } else {
                    L.d(e.toString());
                    mIPublishNewsInterface.publishResult(false);
                }
            }
        });
    }
}
