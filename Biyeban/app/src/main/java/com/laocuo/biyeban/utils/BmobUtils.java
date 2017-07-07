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
import android.widget.ImageView;
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.greendao.User;
import com.laocuo.biyeban.greendao.UserDao;
import com.laocuo.biyeban.main.contacts.ContactsItem;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
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
        if (!TextUtils.isEmpty(objId)) {
            QueryBuilder<User> qb = userDao.queryBuilder();
            qb.where(UserDao.Properties.Objid.eq(objId)).build();
            List<User> userList = qb.list();
//            L.d("bindUserItems userList.size()="+userList.size());
//            L.d("objId = "+objId);
            if (userList.size() <= 0) {
                final User u = new User(null, objId, "", "", "");
                userDao.insert(u);
//                L.d("insert objId = "+objId);

                BmobQuery<BiyebanUser> query = new BmobQuery<BiyebanUser>();
                query.getObject(objId, new QueryListener<BiyebanUser>() {

                    @Override
                    public void done(BiyebanUser user, BmobException e) {
                        if (user != null) {
                            u.setUsername(user.getUsername());
                            u.setAlias(user.getAlias());
                            u.setAvatar(user.getAvatar() == null ? "" : user.getAvatar().getFileUrl());
                            userDao.update(u);
                            String name = TextUtils.isEmpty(u.getAlias()) ? u.getUsername() : u.getAlias();
                            n.setText(name);
                            String avatar = u.getAvatar();
                            if (avatar != null && !TextUtils.isEmpty(avatar)) {
                                FactoryInterface.setAvatar(context, avatar, a);
                            } else {
                                a.setImageResource(R.drawable.user);
                            }
                        } else {
                            L.d("user == null");
                        }
                    }
                });
            } else {
                User u = userList.get(0);
//                L.d("u.getObjid() = "+u.getObjid());

                if (TextUtils.isEmpty(u.getUsername())) {
                    BmobQuery<BiyebanUser> query = new BmobQuery<BiyebanUser>();
                    query.getObject(u.getObjid(), new QueryListener<BiyebanUser>() {

                        @Override
                        public void done(BiyebanUser user, BmobException e) {
                            if (user != null) {
                                String name = TextUtils.isEmpty(user.getAlias()) ? user.getUsername() : user.getAlias();
                                n.setText(name);
                                String avatar = user.getAvatar() == null ? "" : user.getAvatar().getFileUrl();
                                if (avatar != null && !TextUtils.isEmpty(avatar)) {
                                    FactoryInterface.setAvatar(context, avatar, a);
                                } else {
                                    a.setImageResource(R.drawable.user);
                                }
                            } else {
                                L.d("user == null");
                            }
                        }
                    });
                } else {
                    String name = TextUtils.isEmpty(u.getAlias()) ? u.getUsername() : u.getAlias();
                    n.setText(name);
                    String avatar = u.getAvatar();
                    if (avatar != null && !TextUtils.isEmpty(avatar)) {
                        FactoryInterface.setAvatar(context, avatar, a);
                    } else {
                        a.setImageResource(R.drawable.user);
                    }
                }
            }
        }
    }

    public static void updateUserItem(ContactsItem item, UserDao userDao) {
        if (!TextUtils.isEmpty(item.getObjId())) {
            QueryBuilder<User> qb = userDao.queryBuilder();
            qb.where(UserDao.Properties.Objid.eq(item.getObjId())).build();
            List<User> userList = qb.list();
//            L.d("updateUserItem userList.size() = " + userList.size());
//            L.d("objId = "+item.getObjId());
            if (userList.size() <= 0) {
                User u = new User(null, item.getObjId(), item.getUsername(),
                        item.getAlias(), item.getAvatar());
                userDao.insert(u);
//                L.d("insert objId = "+item.getObjId());
            } else {
                User u = userList.get(0);
                if (false == u.getAvatar().equals(item.getAvatar()) ||
                        false == u.getAlias().equals(item.getAlias())) {
                    u.setAvatar(item.getAvatar());
                    u.setAlias(item.getAlias());
                    userDao.update(u);
                }
            }
        }
    }
}
