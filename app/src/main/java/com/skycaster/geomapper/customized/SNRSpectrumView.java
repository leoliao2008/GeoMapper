package com.skycaster.geomapper.customized;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.skycaster.geomapper.R;

/**
 * Created by 廖华凯 on 2017/6/26.
 */

public class SNRSpectrumView extends View {
    private int width;
    private int height;
    private TextPaint mTextPaint;
    private Paint mPaint;
    private int[] mColors=new int[]{
            Color.parseColor("#FB041D"),
            Color.parseColor("#FB9804"),
            Color.parseColor("#FBFB04"),
            Color.parseColor("#B0FB04"),
            Color.parseColor("#04FB35")};
    private float[] mPositions=new float[]{0.0f,0.1f,0.2f,0.3f,0.5f};
    private String[] mSNRs=new String[]{"10","20","30","50","100"};
    private Rect mRect=new Rect();
    private float mFontHeight;


    public SNRSpectrumView(Context context) {
        this(context,null);
    }

    public SNRSpectrumView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SNRSpectrumView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);

        mTextPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_type_2));
        mTextPaint.setStrokeWidth(2);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mFontHeight = fontMetrics.descent - fontMetrics.ascent;
        mTextPaint.getTextBounds("SNR",0,3,mRect);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width=MeasureSpec.getSize(widthMeasureSpec);
        height=MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LinearGradient gradient=new LinearGradient(
                mRect.width()+10,
                0,
                width-10,
                height,
                mColors,
                mPositions,
                Shader.TileMode.CLAMP
        );

        mPaint.setShader(gradient);
        canvas.drawText("SNR",0,(height-mFontHeight)/2,mTextPaint);
        canvas.drawRoundRect(new RectF(mRect.width()+10,0,width-10,mFontHeight),5.0f,5.0f,mPaint);
        for(int i=1;i<mPositions.length;i++){
            canvas.drawLine(mRect.width()+width*mPositions[i],mFontHeight,mRect.width()+width*mPositions[i],height-mFontHeight,mTextPaint);
            float textWidth = mTextPaint.measureText(mSNRs[i - 1]);
            canvas.drawText(mSNRs[i-1],mRect.width()+ width *mPositions[i]-textWidth/2,height-mFontHeight/2+5,mTextPaint);
        }
    }
}
