package com.laocuo.biyeban.bigimage;

import android.animation.Animator;
import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;


public class DragCloseLayout extends FrameLayout {

    public interface DragCloseListener {
        void dragclose();
    }

    public static final int DURATION = 500;

    public void setDragCloseListener(DragCloseListener dragCloseListener) {
        mDragCloseListener = dragCloseListener;
    }

    private DragCloseListener mDragCloseListener;

    private View child;
    private float x,y,x1,y1;
    private int screenHeight;
    private ValueAnimator mAnimator;
    private PointF mStart = new PointF(),mStop = new PointF();

    public DragCloseLayout(@NonNull Context context) {
        this(context, null);
    }

    public DragCloseLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        getBackground().setAlpha(255);
        Point p = getScreenSize(context);
        screenHeight = p.y;
        mAnimator = new ValueAnimator();
        mAnimator.setDuration(DURATION);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF p = (PointF) animation.getAnimatedValue();
//                scrollTo(-1 * (int)(p.x - x), -1 * (int)(p.y - y));
                child.setTranslationX(p.x - x);
                child.setTranslationY(p.y - y);
                float distY = Math.abs(p.y - y);
                child.setScaleX(1 - distY/screenHeight);
                child.setScaleY(1 - distY/screenHeight);
                int alpha = (int) (255 * (1 - distY/screenHeight));
                getBackground().setAlpha(alpha);
                x1 = p.x;
                y1 = p.y;
            }
        });
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                scrollTo(0, 0);
                getBackground().setAlpha(255);
                child.setScaleX(1);
                child.setScaleY(1);
                child.setTranslationX(0);
                child.setTranslationY(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = ev.getRawX();
                y = ev.getRawY();
                mStart.set(x, y);
                x1 = x;
                y1 = y;
                break;
            case MotionEvent.ACTION_MOVE: {
                    float x2 = ev.getRawX();
                    float y2 = ev.getRawY();
                    if (y2 > y && y2 - y > Math.abs(x2 - x)) {
                        return true;
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (child == null) {
            child = (View) getChildAt(0);
        }
        if (child == null) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float distX = Math.abs(event.getRawX() - mStart.x);
                float distY = Math.abs(event.getRawY() - mStart.y);
                int alpha = (int) (255 * (1 - distY/screenHeight));
                getBackground().setAlpha(alpha);
                child.setScaleX(1 - distY/screenHeight);
                child.setScaleY(1 - distY/screenHeight);
//                scrollTo(-1 * (int)(event.getRawX() - x), -1 * (int)(event.getRawY() - y));
                child.setTranslationX(event.getRawX() - x);
                child.setTranslationY(event.getRawY() - y);
                x1 = event.getRawX();
                y1 = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                x1 = event.getRawX();
                y1 = event.getRawY();
                mStop.set(x1, y1);
                if (mDragCloseListener == null || y1 - y < screenHeight/4) {
                    mAnimator.setObjectValues(mStop, mStart);
                    mAnimator.setEvaluator(new PointFEvaluator());
                    mAnimator.start();
                } else {
                    mDragCloseListener.dragclose();
                }
                break;
        }
        return true;
    }

    /**
     * 获取屏幕尺寸
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static Point getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            return new Point(display.getWidth(), display.getHeight());
        } else {
            Point point = new Point();
            display.getSize(point);
            return point;
        }
    }
}
