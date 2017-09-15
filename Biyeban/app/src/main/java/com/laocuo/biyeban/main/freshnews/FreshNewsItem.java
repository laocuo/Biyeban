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
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.bigimage.BigImageActivity;
import com.laocuo.biyeban.greendao.UserDao;
import com.laocuo.biyeban.utils.BmobUtils;
import com.laocuo.biyeban.utils.Utils;
import com.laocuo.recycler.entity.MultiItemEntity;
import com.laocuo.recycler.helper.RecyclerViewHelper;

import java.util.ArrayList;


public class FreshNewsItem extends MultiItemEntity {
    public static final int ITEM_TYPE_MULTI_IMAGES = 1;
    public static final int ITEM_TYPE_SINGLE_IMAGE = 2;

    private String objectId;
    private String tableName;
    private String userObjectId;
    private String content;
    private String time;
    private ArrayList<String> pics = null;
    private ArrayList<String> comments = null;
    private FreshNewsImagesAdapter mImgsAdapter = null;
    private UserDao mUserDao;

    public FreshNewsItem(String objectId,
                         String tableName,
                         int itemType,
                         String userObjectId,
                         String content,
                         String time,
                         ArrayList<String> pics,
                         ArrayList<String> comments) {
        super(itemType);
        this.objectId = objectId;
        this.tableName = tableName;
        this.userObjectId = userObjectId;
        this.content = content;
        this.time = time;
        this.pics = pics;
        this.comments = comments;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getUserObjectId() {
        return userObjectId;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public ArrayList<String> getPics() {
        return pics;
    }

    public void setPics(ArrayList<String> pics) {
        this.pics = pics;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public void bindMultiImages(Context context, RecyclerView r) {
        if (pics != null && pics.size() > 0) {
            if (mImgsAdapter == null) {
                mImgsAdapter = new FreshNewsImagesAdapter(context);
            }
            RecyclerViewHelper.initRecyclerViewH(context, r, false, mImgsAdapter);
            mImgsAdapter.setData(pics);
        } else {
            if (mImgsAdapter != null) {
                mImgsAdapter.setData(null);
            }
            r.setAdapter(null);
        }
    }

    public void bindSingleImage(final Context context, ImageView imageView) {
        final String url = pics.get(0);
        Utils.setImage(context, url, imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImageActivity.launch(context, url);
            }
        });
    }

    public void bindCommentsList(Context context, RecyclerView r, UserDao userDao) {
        mUserDao = userDao;
        if (comments != null && comments.size() > 0) {
            CommentAdapter adapter = (CommentAdapter) r.getAdapter();
            if (adapter == null) {
                adapter = new CommentAdapter(context);
                RecyclerViewHelper.initRecyclerViewV(context, r, false, adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        } else {
            r.setAdapter(null);
        }
    }

    public void addComment(String comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
    }

    private class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
        private Context mContext;

        public CommentAdapter(Context context) {
            mContext = context;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.freshnews_comment_list_item, null);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            String[] comment = (comments.get(position)).split("\\|");
            BmobUtils.bindUserItems(holder.name, null, comment[0], mUserDao, mContext);
            holder.content.setText(comment[1]);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView name, content;
        public CommentViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }
}