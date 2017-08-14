package com.skycaster.geomapper.customized;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by 廖华凯 on 2017/7/31.
 */

public class ZoomableImageView extends android.support.v7.widget.AppCompatImageView {
    private int width;
    private int height;
    private GestureDetectorCompat mGestureDetectorCompat;
    private Matrix mMatrix;

    public ZoomableImageView(Context context) {
        this(context,null);
    }

    public ZoomableImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ZoomableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMatrix=new Matrix();
        mGestureDetectorCompat = new GestureDetectorCompat(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                mMatrix.postTranslate(distanceX,distanceY);
                invalidate();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });


        mGestureDetectorCompat.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width=MeasureSpec.getSize(widthMeasureSpec);
        height=MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetectorCompat.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setMatrix(mMatrix);
        super.onDraw(canvas);
    }

    
}
