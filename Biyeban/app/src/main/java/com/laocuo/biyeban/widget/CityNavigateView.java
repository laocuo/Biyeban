package com.laocuo.biyeban.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.laocuo.biyeban.R;

import java.util.List;

/**
 Copyright (C) laocuo <laocuo@163.com>

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

public class CityNavigateView extends View {
    public interface onTouchListener {
        void showChiefView(String text, boolean show);
    }

    private int mWidth, mHeight, mTextHeight, position, mPaddingTop;
    private Paint paint;
    private Rect mBound;
    private int textsize;
    private int selectedColor;
    private int unselectedColor;
    private int yDown, yMove, mTouchSlop;
    private boolean isSlide;
    private List<String> mHeadList = null;
    private onTouchListener listener;

    public CityNavigateView(Context context) {
        this(context, null);
    }

    public CityNavigateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CityNavigateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CityNavigateViewTheme);
        selectedColor = typedArray.getColor(R.styleable.CityNavigateViewTheme_selected_color, Color.BLACK);
        unselectedColor = typedArray.getColor(R.styleable.CityNavigateViewTheme_unselected_color, Color.WHITE);
        float sp = typedArray.getFloat(R.styleable.CityNavigateViewTheme_textSize, 10.0f);
        textsize = sp2px(context, sp);
        typedArray.recycle();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(textsize);
        paint.setColor(unselectedColor);
        mBound = new Rect();
    }

    public void setContent(List<String> content) {
        mHeadList = content;
        invalidate();
    }

    public void setListener(onTouchListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mHeadList == null) {
            super.onDraw(canvas);
            return;
        }
        int x = 0;
        int y = 0;
        for (int i = 0; i < mHeadList.size(); i++) {
            String text = mHeadList.get(i);
            paint.getTextBounds(text, 0, text.length(), mBound);
            x = (mWidth - mBound.width()) / 2;
            y = mPaddingTop + i*mTextHeight + mTextHeight - (mTextHeight  - mBound.height()) / 2;
//            if (i == position - 1) {
//                paint.setColor(selectedColor);
//            } else {
//                paint.setColor(unselectedColor);
//            }
            canvas.drawText(text, x, y, paint);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int y = (int) ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                yDown = y;
                break;
            case MotionEvent.ACTION_MOVE:
                yMove = y;
                int dy = yMove - yDown;
                if (Math.abs(dy) > mTouchSlop) {
                    isSlide = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handleTouchChief(y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isSlide) {
                    handleTouchChief(y);
                }
                break;
            case MotionEvent.ACTION_UP:
                setBackgroundColor(Color.TRANSPARENT);
                listener.showChiefView(null, false);
                break;
        }
        return true;
    }

    private void handleTouchChief(int y) {
        if (y > mPaddingTop) {
            int z = y - mPaddingTop;
            int a = (mHeight - mPaddingTop) / mHeadList.size();
            position = z/a;
            if (position >= mHeadList.size()) {
                position = mHeadList.size() - 1;
            }
            setBackgroundColor(Color.argb(0x66,0x44,0x44,0x44));
            listener.showChiefView(mHeadList.get(position), true);
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = dip2px(getContext(),40);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = dip2px(getContext(),200);
        }
        mWidth = width;
        mHeight = height;
        mPaddingTop = getPaddingTop();
        if (mHeadList != null) {
            mTextHeight = (mHeight - mPaddingTop) / mHeadList.size();
        }
        setMeasuredDimension(width, height);
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int sp2px (Context context, float sp) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }
}
