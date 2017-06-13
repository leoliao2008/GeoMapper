package com.skycaster.geomapper.customized;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;

/**
 * Created by 廖华凯 on 2017/5/16.
 */

public class CompassView extends FrameLayout {
    private View rootView;
    private SensorManager mSensorManager;
    private ImageView iv_compass;
    private TextView tv_compassRead;
    private Sensor mAccelerateSensor;
    private Sensor mMagneticSensor;
    private Context mContext;
    private SensorEventListener mSensorEventListener=new SensorEventListener() {
        private float[] accelerateValue =new float[3];
        private float[] magneticValue =new float[3];
        private float[] R=new float[9];
        private float[] orientationValue=new float[3];
        private float lastRotateDegree;
        private float currentRoteDegree;
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    System.arraycopy(event.values,0, accelerateValue,0,3);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    System.arraycopy(event.values,0, magneticValue,0,3);
                    break;
                default:
                    break;
            }
            SensorManager.getRotationMatrix(R,null, accelerateValue, magneticValue);
            SensorManager.getOrientation(R,orientationValue);
            final double degrees = Math.toDegrees(orientationValue[0]);
            currentRoteDegree = -(float) degrees;
            if(Math.abs(lastRotateDegree-currentRoteDegree)>=1){
                if(mOrientationChangeListener!=null){
                    BaseApplication.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mOrientationChangeListener!=null){
                                mOrientationChangeListener.onOrientationUpdate(degrees);
                            }
                        }
                    });
                }
                RotateAnimation animation=new RotateAnimation(
                        lastRotateDegree,
                        currentRoteDegree,
                        RotateAnimation.RELATIVE_TO_SELF,
                        0.5f,
                        RotateAnimation.RELATIVE_TO_SELF,
                        0.5f);
                animation.setDuration(50);
                animation.setInterpolator(mContext,android.R.interpolator.bounce);
                animation.setFillAfter(true);
                iv_compass.startAnimation(animation);
                lastRotateDegree=currentRoteDegree;
                float temp=-lastRotateDegree;
                if(temp>=0){
                    if(temp==0){
                        tv_compassRead.setText("正北");
                    }else if(lastRotateDegree==90){
                        tv_compassRead.setText("正东");
                    }else if(temp==180){
                        tv_compassRead.setText("正南");
                    }else if(temp>0&&temp<90){
                       tv_compassRead.setText("东北"+ toTwoDigitFormat(90-temp)+"°");
                    }else {
                        tv_compassRead.setText("东南"+ toTwoDigitFormat(temp-90)+"°");
                    }
                }else {
                    if(lastRotateDegree==90){
                        tv_compassRead.setText("正西");
                    }else if(lastRotateDegree==180){
                        tv_compassRead.setText("正南");
                    }else if(lastRotateDegree>0&&lastRotateDegree<90){
                        tv_compassRead.setText("西北"+ toTwoDigitFormat(90-lastRotateDegree)+"°");
                    }else {
                        tv_compassRead.setText("西南"+ toTwoDigitFormat(lastRotateDegree-90)+"°");
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private String toTwoDigitFormat(float source){
        return String.format("%.2f",source);
    }


    public CompassView(@NonNull Context context) {
        this(context,null);
    }

    public CompassView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CompassView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        rootView= LayoutInflater.from(context).inflate(R.layout.widget_compass_view,null);
        iv_compass= (ImageView) rootView.findViewById(R.id.widget_compass_view_iv_compass);
        tv_compassRead= (TextView) rootView.findViewById(R.id.widget_compass_view_tv_compass_read);
        addView(rootView);
        mSensorManager= (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(mSensorEventListener, mAccelerateSensor,SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mSensorEventListener, mMagneticSensor,SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mSensorManager.unregisterListener(mSensorEventListener, mAccelerateSensor);
        mSensorManager.unregisterListener(mSensorEventListener, mMagneticSensor);
        unRegisterOrientationChangeListener();
    }

    private OrientationChangeListener mOrientationChangeListener;

    public void registerOrientationChangeListener(OrientationChangeListener listener){
        mOrientationChangeListener=listener;
    }

    public void unRegisterOrientationChangeListener(){
        mOrientationChangeListener=null;
    }

    public interface OrientationChangeListener{
        void onOrientationUpdate(double newDegree);
    }
}
