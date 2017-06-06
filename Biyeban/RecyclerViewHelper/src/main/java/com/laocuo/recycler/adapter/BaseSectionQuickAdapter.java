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

package com.laocuo.recycler.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.laocuo.recycler.entity.SectionEntity;

import java.util.List;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public abstract class BaseSectionQuickAdapter<T extends SectionEntity> extends BaseQuickAdapter {

    protected static final int SECTION_HEADER_VIEW = 0x00000444;


    public BaseSectionQuickAdapter(Context context, List<T> data) {
        super(context, data);
    }

    public BaseSectionQuickAdapter(Context context) {
        this(context, null);
    }

    @Override
    protected int getDefItemViewType(int position) {
        return ((SectionEntity) mData.get(position)).isHeader ? SECTION_HEADER_VIEW : 0;
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SECTION_HEADER_VIEW) {
            return super.createBaseViewHolder(parent, attachHeadLayoutRes());
        }
        return super.onCreateDefViewHolder(parent, viewType);
    }

    /**
     * @param holder A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseViewHolder holder, Object item) {
        switch (holder.getItemViewType()) {
            case SECTION_HEADER_VIEW:
                convertHead(holder, (T) item);
                break;
            default:
                convert(holder, (T) item);
                break;
        }
    }

    /**
     * 绑定头部布局
     *
     * @return
     */
    protected abstract int attachHeadLayoutRes();

    protected abstract void convertHead(BaseViewHolder holder, T item);

    protected abstract void convert(BaseViewHolder holder, T item);


}
