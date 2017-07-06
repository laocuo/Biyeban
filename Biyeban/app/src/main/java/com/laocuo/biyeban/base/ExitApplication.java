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

package com.laocuo.biyeban.base;

import java.util.LinkedList;
import java.util.List;


public class ExitApplication {
    private List<BaseActivity> activityList = new LinkedList<BaseActivity>();
    private static ExitApplication instance;
    private ExitApplication() {
    }
    // 单例模式中获取唯一的ExitApplication实例
    public static ExitApplication getInstance() {
        if (null == instance) {
            instance = new ExitApplication();
        }
        return instance;
    }
    // 将Activity添加到容器中
    public void addActivity(BaseActivity activity) {
        activityList.add(activity);
    }
    // 当要退出Activity时，遍历所有Activity 并finish
    public void exit() {
        for (BaseActivity activity : activityList) {
            activity.finish();
        }
//        System.exit(0);
    }

}
