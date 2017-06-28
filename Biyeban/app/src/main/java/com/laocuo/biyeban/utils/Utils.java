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


import android.content.Intent;

import com.laocuo.biyeban.base.BaseActivity;
import com.laocuo.biyeban.graduation.GraduationActivity;
import com.laocuo.biyeban.bmob.BiyebanUser;
import com.laocuo.biyeban.login.LoginActivity;
import com.laocuo.biyeban.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;

public class Utils {
    public static final String CHATROOM = "_chatroom";
    public static final String FRESHNEWS = "_freshnews";
    private static final String AVATAR = "avatar.jpg";

    public static void enterMain(BaseActivity activity) {
        final BiyebanUser user = BmobUser.getCurrentUser(BiyebanUser.class);
        if (user != null) {
            if (user.getGraduClass() == null) {
                activity.startActivity(new Intent(activity, GraduationActivity.class));
            } else {
                activity.startActivity(new Intent(activity, MainActivity.class));
            }
        } else {
            activity.startActivity(new Intent(activity, LoginActivity.class));
        }
//            overridePendingTransition(R.anim.hold, R.anim.zoom_in_exit);
    }

    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String time = format.format(new Date(System.currentTimeMillis()));
        return time;
    }
}
