package com.skycaster.geomapper.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/5/12.
 */

public class BaseApplication extends Application {
    private static Context mContext;
    private static Handler mHandler;
    private static DisplayMetrics displayMetrics;

    private ArrayList<BaseActivity> mActivities=new ArrayList<>();
    private static int mStatusBarHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getApplicationContext();
        //百度地图启动前需初始化
        SDKInitializer.initialize(mContext);
        //fresco初始化
        Fresco.initialize(mContext);
        mHandler=new Handler();
        int id = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(id>0){
            mStatusBarHeight = getResources().getDimensionPixelSize(id);
        }
    }

    public static int getStatusBarHeight() {
        return mStatusBarHeight;
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

    public static DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }

    public static void setDisplayMetrics(DisplayMetrics displayMetrics) {
        BaseApplication.displayMetrics = displayMetrics;
    }

    public static void removeCallBack(Runnable runnable){
        mHandler.removeCallbacks(runnable);
    }

}
