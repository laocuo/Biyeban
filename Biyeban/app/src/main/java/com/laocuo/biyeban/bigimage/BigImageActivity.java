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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseActivity;
import com.laocuo.biyeban.utils.BmobFileListener;
import com.laocuo.biyeban.utils.BmobUtils;
import com.laocuo.biyeban.utils.DensityUtil;
import com.laocuo.biyeban.utils.Utils;
import com.lljjcoder.citylist.Toast.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;


public class BigImageActivity extends BaseActivity<BigImagePresenter>
        implements IBigImageInterface,
        ViewPager.OnPageChangeListener,
        BigImageAdapter.OnTapListener, SaveImageDialogFragment.SaveImageListener, DragCloseLayout.DragCloseListener {
    private static final String PHOTO_SETS_KEY = "PhotoSetsKey";
    private static final String PHOTO_SETS_POS = "PhotoSetsPos";

//    @BindView(R.id.toolbar)
//    Toolbar mToolbar;
    @BindView(R.id.dragclose_layout)
    DragCloseLayout mDragCloseLayout;
    @BindView(R.id.bigimages)
    ViewPager mBigImages;
    @BindView(R.id.bigimageindex)
    TextView mIndex;

    @Inject
    BigImageAdapter mBigImageAdapter;

    private ArrayList<BigImageItem> mPhotoSetsId;
    private int mCurrentItem, length, move_dis;
    private boolean isHide;
    private SaveImageDialogFragment mSaveImageDialogFragment;

    public static void launch(View v, Context context, int pos, ArrayList<String> photoId) {
        Intent intent = new Intent(context, BigImageActivity.class);
        intent.putExtra(PHOTO_SETS_POS, pos);
        ArrayList<BigImageItem> bigImageItemList = new ArrayList<>();
        for (String photoUrl : photoId) {
            bigImageItemList.add(new BigImageItem(photoUrl));
        }
        intent.putParcelableArrayListExtra(PHOTO_SETS_KEY, bigImageItemList);
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, v, "share_image");
        context.startActivity(intent,optionsCompat.toBundle());
    }

    public static void launch(View v, Context context, String photoUrl) {
        Intent intent = new Intent(context, BigImageActivity.class);
        intent.putExtra(PHOTO_SETS_POS, 0);
        ArrayList<BigImageItem> bigImageItemList = new ArrayList<>();
            bigImageItemList.add(new BigImageItem(photoUrl));
        intent.putParcelableArrayListExtra(PHOTO_SETS_KEY, bigImageItemList);
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, v, "share_image");
        context.startActivity(intent,optionsCompat.toBundle());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bigimage;
    }

    @Override
    protected void doInject() {
        mPhotoSetsId = getIntent().getParcelableArrayListExtra(PHOTO_SETS_KEY);
        length = mPhotoSetsId.size();
        mCurrentItem = getIntent().getIntExtra(PHOTO_SETS_POS, 0);
        DaggerBigImageComponent.builder()
                .bigImageModule(new BigImageModule(this, mPhotoSetsId))
                .build()
                .inject(this);
        mBigImages.setAdapter(mBigImageAdapter);
        mBigImages.setOffscreenPageLimit(mPhotoSetsId.size());
        mBigImages.addOnPageChangeListener(this);
        mBigImages.setCurrentItem(mCurrentItem);
        mBigImageAdapter.setTapListener(this);
        mBigImageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void doInit() {
//        hideStatusBar(true);
//        initToolBar(mToolbar, true , "");
        mDragCloseLayout.setDragCloseListener(this);
        updateIndex();
        mSaveImageDialogFragment = new SaveImageDialogFragment();
        mSaveImageDialogFragment.setListener(this);
    }

    @Override
    protected void getData(boolean isRefresh) {

    }

    private void updateIndex() {
        int current = mCurrentItem + 1;
        mIndex.setText(current+"/"+length);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentItem = position;
        updateIndex();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPhotoClick() {
        isHide = !isHide;
        move_dis = DensityUtil.dip2px(this, 20) + mIndex.getHeight();
        if (isHide) {
            mIndex.animate().translationYBy(move_dis).setDuration(300);
//            mToolbar.animate().translationY(-mToolbar.getBottom()).setDuration(300);
        } else {
//            mToolbar.animate().translationY(0).setDuration(300);
            mIndex.animate().translationYBy(-move_dis).setDuration(300);
        }
    }

    @Override
    public boolean onPhotoLongCLick() {
        mSaveImageDialogFragment.show(getSupportFragmentManager(), "saveImage");
        return true;
    }

    @Override
    public void confirm() {
        //save image
        BigImageItem currentItem = mPhotoSetsId.get(mCurrentItem);
        BmobUtils.downloadBmobFile(currentItem.getUrl(),
                Utils.getPicsPath(BigImageActivity.this),
                new BmobFileListener() {
                    @Override
                    public void success() {
                        ToastUtils.showShortToast(BigImageActivity.this, "图片已保存");
                    }

                    @Override
                    public void fail() {
                        ToastUtils.showShortToast(BigImageActivity.this, "图片保存失败");
                    }
                });
    }

    @Override
    public void dragclose() {
        finishAfterTransition();
    }
}
