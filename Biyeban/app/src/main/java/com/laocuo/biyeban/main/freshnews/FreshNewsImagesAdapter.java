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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.bigimage.BigImageActivity;
import com.laocuo.biyeban.utils.FactoryInterface;
import com.laocuo.biyeban.utils.L;

import java.util.ArrayList;

public class FreshNewsImagesAdapter extends RecyclerView.Adapter<FreshNewsImagesAdapter.ImageHolder>{
    private Context mContext;
    private ArrayList<String> pics = new ArrayList<>();

    public FreshNewsImagesAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageHolder imageHolder = new ImageHolder(LayoutInflater.from(mContext)
            .inflate(R.layout.image_list_item_l, parent, false));
        return imageHolder;
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, final int position) {
        FactoryInterface.setImage(mContext,
                pics.get(position),
                holder.mImageView);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.d("FreshNewsImagesAdapter onClick="+position);
                BigImageActivity.launch(mContext, position, pics);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pics == null ? 0 : pics.size();
    }

    public void setData(ArrayList<String> data) {
        if (data != null) {
            pics.clear();
            pics.addAll(data);
        } else {
            pics.clear();
        }
        notifyDataSetChanged();
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        public ImageHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.pic_l);
        }
    }
}
