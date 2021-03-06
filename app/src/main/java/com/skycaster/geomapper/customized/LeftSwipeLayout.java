package com.skycaster.geomapper.customized;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.skycaster.geomapper.util.LogUtil;

/**
 * Created by 廖华凯 on 2017/6/22.
 */

public class LeftSwipeLayout extends FrameLayout {
    private View childTop;
    private View childBeneath;
    private ViewDragHelper mViewDragHelper;
    private int dragRange;
    public LeftSwipeLayout(@NonNull Context context) {
        this(context,null);
    }

    public LeftSwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }


    public LeftSwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if(getChildCount()==2){
                    childTop= getChildAt(1);
                    childBeneath= getChildAt(0);
                    dragRange=childBeneath.getMeasuredWidth();
                    mViewDragHelper=ViewDragHelper.create(LeftSwipeLayout.this, new ViewDragHelper.Callback() {
                        @Override
                        public boolean tryCaptureView(View child, int pointerId) {
                            return child.equals(childTop);
                        }

                        @Override
                        public void onViewReleased(View releasedChild, float xvel, float yvel) {
                            super.onViewReleased(releasedChild, xvel, yvel);
                            int left = Math.abs(releasedChild.getLeft());
                            if(left>=dragRange/2){
                                mViewDragHelper.settleCapturedViewAt(-dragRange,0);
                            }else {
                                mViewDragHelper.settleCapturedViewAt(0,0);
                            }
                            invalidate();
                        }

                        @Override
                        public int clampViewPositionHorizontal(View child, int left, int dx) {
                            int i = Math.max(-dragRange, Math.min(0, left));
                            //这是为了不要跟上层的父view滑动冲突：我还没滑到最左端，父view不要抢我的事件，我滑完了，父view重拾主导权。
                            if(i>-dragRange){
                                requestDisallowInterceptTouchEvent(true);
                            }else {
                                requestDisallowInterceptTouchEvent(false);
                            }
                            return i;
                        }

                        @Override
                        public int getViewHorizontalDragRange(View child) {
                            return dragRange;
                        }

                    });
                }

            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mViewDragHelper!=null){
            mViewDragHelper.processTouchEvent(event);
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mViewDragHelper!=null){
            return mViewDragHelper.shouldInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if(mViewDragHelper!=null&&mViewDragHelper.continueSettling(true)){
            invalidate();
        }else {
            super.computeScroll();
        }
    }

    private void showLog(String msg){
        LogUtil.showLog(getClass().getSimpleName(),msg);
    }
}
