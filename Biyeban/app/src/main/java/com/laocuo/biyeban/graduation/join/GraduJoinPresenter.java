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

package com.laocuo.biyeban.graduation.join;


import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.bmob.GraduClass;
import com.laocuo.biyeban.utils.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class GraduJoinPresenter implements IGraduJoinPresenter {
    private IGraduJoinView mView;
    private List<HashMap<String, String>> mClassList = new ArrayList<>();
    private List<GraduClass> gradu_list;

    @Override
    public void loadData() {
        BmobQuery<GraduClass> query = new BmobQuery<>();
        query.findObjects(new FindListener<GraduClass>() {
            @Override
            public void done(List<GraduClass> list, BmobException e) {
                if (e == null) {
                    expandClassList(list);
                    mView.loadData(mClassList);
                } else {
                    L.d(e.toString());
                }
            }
        });
    }

    private void expandClassList(List<GraduClass> list) {
        gradu_list = list;
        mClassList.clear();
        for (GraduClass gc : list) {
            HashMap<String, String> item = new HashMap<>();
            item.put("name", gc.getClassName());
            item.put("district", gc.getDistrict());
            item.put("year", gc.getGraduYear());
            mClassList.add(item);
        }
    }

    @Override
    public void loadMoreData() {

    }

    @Inject
    GraduJoinPresenter(IGraduJoinView view) {
        mView = view;
    }

    @Override
    public void queryGraduClass(String key) {

    }

    @Override
    public void joinGraduClass(int pos) {
        final BiyebanUser user = BmobUser.getCurrentUser(BiyebanUser.class);
        final GraduClass gc = gradu_list.get(pos);
        String objId = gc.getObjectId();
        ArrayList<String> classmates = gc.getClassmates();
        classmates.add(user.getObjectId());
        GraduClass graduClass = new GraduClass();
        graduClass.setClassmates(classmates);
        graduClass.update(objId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    updateCurrentUser(gc);
                } else {
                    L.d(e.toString());
                    mView.joinGraduClass(false);
                }
            }
        });
    }

    private void updateCurrentUser(GraduClass gc) {
        final BiyebanUser user = BmobUser.getCurrentUser(BiyebanUser.class);
        if (user != null) {
            BiyebanUser u = new BiyebanUser();
            u.setGraduClass(gc.getObjectId());
            u.update(user.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        mView.joinGraduClass(true);
                    } else {
                        L.d(e.toString());
                        mView.joinGraduClass(false);
                    }
                }
            });
        }
    }
}
