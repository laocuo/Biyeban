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


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laocuo.biyeban.BiyebanApp;
import com.laocuo.biyeban.R;
import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.greendao.DaoSession;
import com.laocuo.biyeban.greendao.User;
import com.laocuo.biyeban.greendao.UserDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
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
//            BiyebanApp.getInstance().getDaoSession().getUserDao().deleteAll();
        }
    }

    public static void downloadBmobFile(String url, String path, final BmobFileListener l) {
        int index = url.lastIndexOf("/");
        String filename = url.substring(index);
        String file_path = path + filename;
        if (!TextUtils.isEmpty(filename)) {
            File file = new File(file_path);
            BmobFile bmobFile = new BmobFile();
            bmobFile.setUrl(url);
            bmobFile.download(file, new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if (e != null) {
                        L.i("bmob", "文件下载失败：" + e.getMessage() + "," + e.getErrorCode());
                        l.fail();
                    } else {
                        L.d("bmob", "文件下载成功");
                        l.success();
                    }
                }

                @Override
                public void onProgress(Integer integer, long l) {

                }
            });
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

    public static void uploadBmobFiles(List<String> files, final UploadBmobFilesListener l) {
        L.d("bmob", "uploadBmobFiles:"+files.toString());
        final int size = files.size();
        String[] filePaths = files.toArray(new String[size]);
        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
                if (list1.size() == size) {
                    //TODO upload all files complete
                    L.i("bmob", "文件上传成功，返回的名称--" + list1.toString());
                    l.success(list1);
                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {
                L.d("bmob", "uploadBmobFiles:"+i+"/"+i2);
            }

            @Override
            public void onError(int i, String s) {
                L.d("bmob", "文件上传失败：" + s);
                l.fail();
            }
        });
    }

    public static void bindUserItems(final TextView n, final ImageView a, final String objId
            , final UserDao userDao, final Context context) {
        if (TextUtils.isEmpty(objId)) {
            return;
        }

        QueryBuilder<User> qb = userDao.queryBuilder();
        qb.where(UserDao.Properties.Objid.eq(objId)).build();
        List<User> userList = qb.list();
        L.d("bindUserItem objId:"+objId);
        if (userList.size() <= 0) {
            L.d("bindUserItem not in DB");
            BmobQuery<BiyebanUser> query = new BmobQuery<BiyebanUser>();
            query.getObject(objId, new QueryListener<BiyebanUser>() {

                @Override
                public void done(BiyebanUser user, BmobException e) {
                    if (user != null) {
                        L.d("bindUserItem get from network");
                        String avatar = user.getAvatar() == null ? "" : user.getAvatar().getFileUrl();
                        String alias = TextUtils.isEmpty(user.getAlias()) ? user.getUsername() : user.getAlias();
                        userDao.insert(new User(null, user.getObjectId(), user.getUsername(),
                                alias, avatar));
                        L.d("bindUserItem insert DB");
                        n.setText(alias);
                        if (a != null) {
                            if (!TextUtils.isEmpty(avatar)) {
                                Utils.setAvatar(context, avatar, a);
                            } else {
                                a.setImageResource(R.drawable.user);
                            }
                        }
                    } else {
                        L.d("user == null");
                    }
                }
            });
        } else {
            User u = userList.get(0);
            L.d("bindUserItem find in DB");
            L.d("bindUserItem username is "+u.getUsername());
            String name = TextUtils.isEmpty(u.getAlias()) ? u.getUsername() : u.getAlias();
            n.setText(name);
            if (a != null) {
                String avatar = u.getAvatar();
                if (avatar != null && !TextUtils.isEmpty(avatar)) {
                    Utils.setAvatar(context, avatar, a);
                } else {
                    a.setImageResource(R.drawable.user);
                }
            }
        }

        //点击头像更新用户信息
        if (a != null) {
            a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(objId)) {
                        BmobQuery<BiyebanUser> query = new BmobQuery<BiyebanUser>();
                        query.getObject(objId, new QueryListener<BiyebanUser>() {

                            @Override
                            public void done(BiyebanUser user, BmobException e) {
                                if (user != null) {
                                    String name = TextUtils.isEmpty(user.getAlias()) ? user.getUsername() : user.getAlias();
                                    String avatar = user.getAvatar() == null ? "" : user.getAvatar().getFileUrl();
                                    QueryBuilder<User> qb = userDao.queryBuilder();
                                    qb.where(UserDao.Properties.Objid.eq(objId)).build();
                                    List<User> userList = qb.list();
                                    if (userList.size() <= 0) {
                                        User u = new User(null, user.getObjectId(), user.getUsername(),
                                                name, avatar);
                                        userDao.insert(u);
                                    } else {
                                        User u = userList.get(0);
                                        u.setAlias(name);
                                        u.setAvatar(avatar);
                                        userDao.update(u);
                                    }
                                    n.setText(name);
                                    if (!TextUtils.isEmpty(avatar)) {
                                        Utils.setAvatar(context, avatar, a);
                                    } else {
                                        a.setImageResource(R.drawable.user);
                                    }
                                } else {
                                    L.d("user == null");
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public static void clearCache() {
        DaoSession daoSession = BiyebanApp.getInstance().getDaoSession();
        daoSession.getUserDao().deleteAll();
    }
}
