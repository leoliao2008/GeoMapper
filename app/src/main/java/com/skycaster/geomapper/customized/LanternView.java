package com.skycaster.geomapper.customized;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.skycaster.geomapper.R;
import com.skycaster.inertial_navi_lib.FixQuality;


/**
 * Created by 廖华凯 on 2017/6/16.
 */

public class LanternView extends View {
    private int width;
    private int height;
    private Paint mPaint;
    private TextPaint mTextPaint;
    private String desc;
    private Rect mTextBound=new Rect();
    private int mTextHeight;
    private float mRadius;
    private float mTextSize;


    public LanternView(Context context) {
        this(context,null);
    }

    public LanternView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LanternView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint=new TextPaint();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LanternView);
        mTextSize=typedArray.getDimension(R.styleable.LanternView_lantern_view_textSize,15.f);
        mTextPaint.setTextSize(mTextSize);
        typedArray.recycle();
    }

    public void updateLantern(FixQuality fixQuality){
        switch (fixQuality){
            case QUALITY_INVALID:
                mPaint.setColor(Color.BLACK);
                mTextPaint.setColor(Color.BLACK);
                desc=getResources().getString(R.string.invalid_fix_quality);
                break;
            case QUALITY_GPS_FIX:
                mPaint.setColor(Color.GRAY);
                mTextPaint.setColor(Color.GRAY);
                desc=getResources().getString(R.string.gps_fix_quality);
                break;
            case QUALITY_DGPS_FIX:
                mPaint.setColor(Color.RED);
                mTextPaint.setColor(Color.RED);
                desc=getResources().getString(R.string.dgps_fix_quality);
                break;
            case QUALITY_FLOAT_RTK:
                mPaint.setColor(Color.BLUE);
                mTextPaint.setColor(Color.BLUE);
                desc=getResources().getString(R.string.flaot_fix_quality);
                break;
            case QUALITY_REAL_TIME_KINEMATIC:
                mPaint.setColor(getResources().getColor(R.color.colorPrimary));
                mTextPaint.setColor(getResources().getColor(R.color.colorPrimary));
                desc=getResources().getString(R.string.rtk_fix_quality);
                break;
            default:
                mPaint.setColor(Color.TRANSPARENT);
                mTextPaint.setColor(Color.TRANSPARENT);
                desc=getResources().getString(R.string.default_fix_quality);
                break;
        }

        mTextPaint.getTextBounds(desc,0,desc.length(),mTextBound);
        mTextHeight=mTextBound.height();
        mRadius =(height-mTextHeight)/2;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width=MeasureSpec.getSize(widthMeasureSpec);
        height=MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(width/2, mRadius, mRadius,mPaint);
        if(!TextUtils.isEmpty(desc)){
            canvas.drawText(desc,(width-mTextBound.width())/2,height-1,mTextPaint);
        }
    }
}
