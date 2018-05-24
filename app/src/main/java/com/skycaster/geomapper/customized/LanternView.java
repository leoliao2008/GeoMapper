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
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.skycaster.geomapper.R;
import com.skycaster.gps_decipher_lib.GPGGA.FixQuality;


/**
 * Created by 廖华凯 on 2017/6/16.
 */

public class LanternView extends View {
    private int width;
    private int height;
    private Paint mPaint;
    private TextPaint mTextPaint;
    private String mLevel;
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
        mTextPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LanternView);
        DisplayMetrics metrics=new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        mTextSize=typedArray.getDimension(R.styleable.LanternView_lantern_view_textSize,12.f)*metrics.scaledDensity;
        mTextPaint.setTextSize(mTextSize);
        setBackgroundResource(R.drawable.shape_lantern_view);
        typedArray.recycle();
    }

    public void updateLantern(FixQuality fixQuality){
        switch (fixQuality){
            case QUALITY_GPS_FIX:
                mPaint.setColor(Color.parseColor("#FF6004"));
                mTextPaint.setColor(Color.parseColor("#FF6004"));
                mLevel =getResources().getString(R.string.gps_fix_quality);
                break;
            case QUALITY_DGPS_FIX:
                mPaint.setColor(Color.parseColor("#FB9804"));
                mTextPaint.setColor(Color.parseColor("#FB9804"));
                mLevel =getResources().getString(R.string.dgps_fix_quality);
                break;
            case QUALITY_FLOAT_RTK:
                mPaint.setColor(Color.parseColor("#90C451"));
                mTextPaint.setColor(Color.parseColor("#90C451"));
                mLevel =getResources().getString(R.string.flaot_fix_quality);
                break;
            case QUALITY_REAL_TIME_KINEMATIC:
                mPaint.setColor(Color.parseColor("#007029"));
                mTextPaint.setColor(Color.parseColor("#007029"));
                mLevel =getResources().getString(R.string.rtk_fix_quality);
                break;
            case QUALITY_INVALID:
            default:
                mPaint.setColor(Color.parseColor("#FF0000"));
                mTextPaint.setColor(Color.parseColor("#FF0000"));
                mLevel =getResources().getString(R.string.invalid_fix_quality);
                break;
        }

        mTextPaint.getTextBounds(mLevel,0, mLevel.length(),mTextBound);
        mTextHeight=mTextBound.height();
        mRadius =(height-mTextHeight)/2-3;
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
        if(!TextUtils.isEmpty(mLevel)){
            canvas.drawText(mLevel,(width-mTextBound.width())/2,height-3,mTextPaint);
        }else{
            updateLantern(FixQuality.QUALITY_INVALID);
        }
    }
}
