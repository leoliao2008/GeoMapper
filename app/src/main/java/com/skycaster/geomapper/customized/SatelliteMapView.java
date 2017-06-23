package com.skycaster.geomapper.customized;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.location.GpsSatellite;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.skycaster.geomapper.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 廖华凯 on 2017/6/23.
 */

public class SatelliteMapView extends SurfaceView {

    private SurfaceHolder mSurfaceHolder;
    private float innerRadius;
    private float outerRadius;
    private int canvasWidth;
    private int canvasHeight;
    private Paint mPaintDarkBlue;
    private Paint mPaintBlue;
    private Paint mPaintWhiteSolid;
    private Paint mPaintWhiteDot;
    private Paint mPaintSatellite;
    private TextPaint mTextPaintFontSmall;
    private TextPaint mTextPaintFontBig;
    private float centerX;
    private float centerY;
    private AtomicBoolean isFirstTimeDrawn=new AtomicBoolean(true);
    private double mFontHeight;

    public SatelliteMapView(Context context) {
        this(context,null);
    }

    public SatelliteMapView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SatelliteMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = getHolder();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SatelliteMapView);
        innerRadius =typedArray.getDimension(R.styleable.SatelliteMapView_radius, 400);
        typedArray.recycle();

        mPaintBlue =new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBlue.setColor(Color.parseColor("#00003E"));

        mPaintDarkBlue=new Paint(mPaintBlue);
        mPaintDarkBlue.setColor(Color.parseColor("#01152E"));


        mPaintWhiteSolid =new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintWhiteSolid.setColor(Color.parseColor("#B5B5B6"));
        mPaintWhiteSolid.setStyle(Paint.Style.STROKE);
        mPaintWhiteSolid.setStrokeWidth(1);

        mPaintWhiteDot=new Paint(mPaintWhiteSolid);
        mPaintWhiteDot.setPathEffect(new DashPathEffect(new float[]{9,9},0));

        mTextPaintFontSmall=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaintFontSmall.setColor(Color.parseColor("#B5B5B6"));
        mTextPaintFontSmall.setTextSize(getResources().getDimension(R.dimen.text_size_for_satellite_view_small));
        mTextPaintFontSmall.setStrokeWidth(2);

        mTextPaintFontBig=new TextPaint(mTextPaintFontSmall);
        mTextPaintFontBig.setTextSize(getResources().getDimension(R.dimen.text_size_for_satellite_view_big));
        mTextPaintFontBig.setStrokeWidth(4);
        Paint.FontMetrics fontMetrics = mTextPaintFontBig.getFontMetrics();
        mFontHeight = Math.ceil(fontMetrics.descent - fontMetrics.ascent-fontMetrics.leading*2);
        outerRadius= (float) (innerRadius + mFontHeight);

        mPaintSatellite=new Paint(Paint.ANTI_ALIAS_FLAG);

        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                showLog("surfaceCreated");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                showLog("surfaceChanged");

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                showLog("surfaceDestroyed");

            }
        });

        setLayerType(LAYER_TYPE_SOFTWARE,null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        canvasWidth=MeasureSpec.getSize(widthMeasureSpec);
        canvasHeight=MeasureSpec.getSize(heightMeasureSpec);
        centerX=canvasWidth/2;
        centerY=canvasHeight/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isFirstTimeDrawn.compareAndSet(true,false)){
            drawCompass(canvas);
        }
    }

    /**
     * 绘制罗盘
     * @param canvas
     */
    private void drawCompass(Canvas canvas) {

        //画外部实心圆
        canvas.drawCircle(centerX, centerY, outerRadius, mPaintDarkBlue);
        //画内部实心圆
        canvas.drawCircle(centerX, centerY, innerRadius, mPaintBlue);
        //画最外层两个实线圈圈
        canvas.drawCircle(centerX, centerY, outerRadius, mPaintWhiteSolid);
        canvas.drawCircle(centerX, centerY, innerRadius, mPaintWhiteSolid);
        //画内部4个虚线圈圈
        for(int i=1;i<5;i++){
            canvas.drawCircle(centerX, centerY, innerRadius / 5 * i, mPaintWhiteDot);
        }
        //分别画24条标识角度的虚线，以圆心为终点
        for(int i=1;i<25;i++){
            int degree = 15 * i;
            double lineAngle=degree*Math.PI/180;
            float startX= (float) (centerX+Math.cos(lineAngle)*(innerRadius));
            float startY= (float) (centerY-Math.sin(lineAngle)*(innerRadius));
            float stopX=centerX;
            float stopY=centerY;
            canvas.drawLine(startX,startY,stopX,stopY,mPaintWhiteDot);
            //每条虚线起始位置绘制表示其度数的文字
            String info;
            TextPaint textPaint=mTextPaintFontSmall;
            switch (degree){
                case 90:
                    info="N";
                    textPaint=mTextPaintFontBig;
                    break;
                default:
                    int temp=90-degree;
                    if(temp<0){
                        temp+=360;
                    }
                    info=temp +"°";
                    break;
            }
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            double deviation= Math.ceil(fontMetrics.descent - fontMetrics.ascent-fontMetrics.leading*2)/2;
            if(degree!=90){
                startX= (float) (centerX+Math.cos(lineAngle)*(innerRadius + mFontHeight/2-deviation));
                startY= (float) (centerY-Math.sin(lineAngle)*(innerRadius + mFontHeight/2-deviation));
            }else {
                startX= (float) (centerX+Math.cos(lineAngle)*(innerRadius + 15));
                startY= (float) (centerY-Math.sin(lineAngle)*(innerRadius + 15));
            }

            //旋转文字使其方向与园外径相切
            canvas.rotate(90-degree,startX,startY);
            float textWidth = textPaint.measureText(info);
            //设置文字起始点
            float textStartX=startX-textWidth/2;
            float textStartY=startY;
            canvas.drawText(info,textStartX,textStartY,textPaint);
            //返回默认的旋转角度
            canvas.rotate(degree-90,startX,startY);
        }
    }

    public void drawSatellites(ArrayList<GpsSatellite>list){
        showLog("drawSatellites......");
        Canvas canvas = mSurfaceHolder.lockCanvas(null);
        drawCompass(canvas);
        for(GpsSatellite satellite:list){
            float elevation = satellite.getElevation();
            float r = innerRadius * (elevation / 90);
            double degree = (90 - satellite.getAzimuth() + 360) * Math.PI / 180;
            float x= (float) (centerX+Math.cos(degree)*r);
            float y= (float) (centerY+Math.sin(degree)*r);
            float snr = satellite.getSnr();
            if(snr<=10){
                mPaintSatellite.setColor(Color.parseColor("#FB041D"));
            }else if(snr<=20){
                mPaintSatellite.setColor(Color.parseColor("#FB9804"));
            }else if(snr<=30){
                mPaintSatellite.setColor(Color.parseColor("#FBFB04"));
            }else if(snr<=50){
                mPaintSatellite.setColor(Color.parseColor("#B0FB04"));
            }else {
                mPaintSatellite.setColor(Color.parseColor("#04FB35"));
            }
            canvas.drawCircle(x,y,20,mPaintSatellite);
        }
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }
}
