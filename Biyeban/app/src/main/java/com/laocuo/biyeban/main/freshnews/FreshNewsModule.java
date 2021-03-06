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

package com.laocuo.biyeban.main.freshnews;

import android.content.Context;


import com.laocuo.biyeban.BiyebanApp;
import com.laocuo.biyeban.greendao.DaoSession;

import dagger.Module;
import dagger.Provides;


@Module
public class FreshNewsModule {
    private FreshNewsFragment mFreshNewsFragment;

    public FreshNewsModule(FreshNewsFragment fragment) {
        mFreshNewsFragment = fragment;
    }

    @Provides
    IFreshNewsView provideIFreshNewsView() {
        return (IFreshNewsView)mFreshNewsFragment;
    }

    @Provides
    Context provideContext() {
        return mFreshNewsFragment.getContext();
    }

    @Provides
    DaoSession provideDaoSession() {
        return BiyebanApp.getInstance().getDaoSession();
    }
}
