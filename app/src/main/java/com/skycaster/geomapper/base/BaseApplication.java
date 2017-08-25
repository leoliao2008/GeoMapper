package com.skycaster.geomapper.base;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.skycaster.geomapper.data.StaticData;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/5/12.
 */

public class BaseApplication extends Application {
    private static Context mContext;
    private static Handler mHandler;
    private static DisplayMetrics displayMetrics;
    private static SharedPreferences mSharedPreferences;
    private ArrayList<BaseActivity> mActivities=new ArrayList<>();
    private static int mStatusBarHeight;
    private static BluetoothSocket bluetoothSocket;
    private static BluetoothDevice bluetoothDevice;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getApplicationContext();
        //百度地图启动前需初始化
        SDKInitializer.initialize(mContext);
        //fresco初始化
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(mContext)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(mContext,config);

        mHandler=new Handler();
        int id = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(id>0){
            mStatusBarHeight = getResources().getDimensionPixelSize(id);
        }

        mSharedPreferences=getSharedPreferences(StaticData.SP_NAME,MODE_PRIVATE);

        //解决7.0提供自身文件给其它应用使用时，如果给出一个file://格式的URI的话，应用会抛出FileUriExposedException。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
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

    public static int getScreenHeight(){
        return getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(){
        return getDisplayMetrics().widthPixels;
    }

    public static void removeCallBacks(Runnable runnable){
        mHandler.removeCallbacks(runnable);
    }

    public static void showToast(String msg){
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }

    public static SharedPreferences getSharedPreferences(){
        return mSharedPreferences;
    }

    public static BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public static void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        BaseApplication.bluetoothSocket = bluetoothSocket;
    }

    public static BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public static void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        BaseApplication.bluetoothDevice = bluetoothDevice;
    }
}
