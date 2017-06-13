package com.skycaster.geomapper.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;

import project.SerialPort.SerialPort;

/**
 * Created by 廖华凯 on 2017/5/12.
 */

public class BaseApplication extends Application {
    private static Context mContext;
    private static Handler mHandler;
    private ArrayList<BaseActivity> mActivities=new ArrayList<>();
    private static SerialPort stcSerialPort;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getApplicationContext();
        mHandler=new Handler();
    }

    public void addToStack(BaseActivity activity){
        if(!mActivities.contains(activity)){
            mActivities.add(activity);
        }
    }

    public void removeFromStack(BaseActivity activity){
        mActivities.remove(activity);
    }

    public int getActivitiesCount(){
        return mActivities.size();
    }

    public static Context getContext() {
        return mContext;
    }

    public static void post(Runnable runnable){
        mHandler.post(runnable);
    }

    public static void postDelay(Runnable runnable,long millis){
        mHandler.postDelayed(runnable,millis);
    }

    public static void removeCallBack(Runnable runnable){
        mHandler.removeCallbacks(runnable);
    }

    public static void setSerialPort(SerialPort paramSerialPort){
        stcSerialPort=paramSerialPort;
    }

    public static SerialPort getSerialPort(){
        return stcSerialPort;
    }

    public static void closeSerialPort(){
        if(stcSerialPort!=null){
            stcSerialPort.close();
            stcSerialPort=null;
        }
    }

}
