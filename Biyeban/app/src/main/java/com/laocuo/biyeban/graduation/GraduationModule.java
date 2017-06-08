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

package com.laocuo.biyeban.graduation;

import com.laocuo.biyeban.graduation.create.GraduCreateFragment;
import com.laocuo.biyeban.graduation.main.GraduMainFragment;
import com.laocuo.biyeban.graduation.join.GraduJoinFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class GraduationModule {
    private IGraduationInterface mInterface;
    public GraduationModule (IGraduationInterface i) {
        mInterface = i;
    }

    @Provides
    IGraduationInterface provideIGraduationInterface() {
        return mInterface;
    }

    @Provides
    GraduMainFragment provideGraduMainFragment() {
        return GraduMainFragment.newInstance(mInterface);
    }

    @Provides
    GraduCreateFragment provideGraduCreateFragment() {
        return GraduCreateFragment.newInstance();
    }

    @Provides
    GraduJoinFragment provideGraduJoinFragment() {
        return GraduJoinFragment.newInstance();
    }
}
