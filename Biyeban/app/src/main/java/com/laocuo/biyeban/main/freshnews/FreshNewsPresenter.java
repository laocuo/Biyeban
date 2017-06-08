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

import com.laocuo.biyeban.base.IBasePresenter;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FreshNewsPresenter implements IBasePresenter {
    private IFreshNewsView mIFreshNewsView;
    private List<FreshNewsItem> mFreshNewsItems = new ArrayList<>();
    private List<FreshNewsItem> mFreshNewsMoreItems = new ArrayList<>();
    private int index = 0;

    @Override
    public void loadData() {
        mIFreshNewsView.showLoading();
        for (int i=0;i<20;i++) {
            mFreshNewsItems.add(new FreshNewsItem(FreshNewsItem.ITEM_TYPE_NORMAL,
                    "Normal"+Integer.toString(i)));
        }
        mIFreshNewsView.hideLoading();
        mIFreshNewsView.loadData(mFreshNewsItems);
    }

    @Override
    public void loadMoreData() {
        mFreshNewsMoreItems.clear();
        for (int i=0;i<4;i++) {
            mFreshNewsMoreItems.add(new FreshNewsItem(FreshNewsItem.ITEM_TYPE_NORMAL,
                    "More"+Integer.toString(index)));
            index++;
        }
        mIFreshNewsView.loadMoreData(mFreshNewsMoreItems);
    }

    @Inject
    FreshNewsPresenter(IFreshNewsView view) {
        mIFreshNewsView = view;
    }
}
