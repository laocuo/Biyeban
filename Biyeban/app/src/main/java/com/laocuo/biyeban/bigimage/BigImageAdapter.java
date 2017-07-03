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

package com.laocuo.biyeban.bigimage;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.utils.FactoryInterface;

import java.util.ArrayList;


public class BigImageAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<BigImageItem> mImgList = null;
    private OnTapListener mOnTapListener;

    public BigImageAdapter(Context context, ArrayList<BigImageItem> urls) {
        mContext = context;
        mImgList = urls;
    }

    @Override
    public int getCount() {
        int count = mImgList == null ? 0 : mImgList.size();
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bigimage_item, null, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.bigimage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnTapListener.onPhotoClick();
            }
        });
        FactoryInterface.setImage(mContext, mImgList.get(position).getUrl(), imageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setTapListener(OnTapListener listener) {
        mOnTapListener = listener;
    }

    public interface OnTapListener {
        void onPhotoClick();
    }
}
