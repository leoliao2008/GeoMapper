package com.skycaster.geomapper.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.StaticData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import project.SerialPort.SerialPort;

/**
 * Created by 廖华凯 on 2017/6/14.
 */

public class GPSDataBroadcastingService extends Service {
    private SerialPort mSerialPort;
    private InputStream mInputStream;
    private AtomicBoolean isReceivingData=new AtomicBoolean(false);
    private int bufferSize=512;
    private byte[] temp=new byte[bufferSize];
    private Thread mThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        //如果正在运行，就跳过，保证同一时间只有一个服务线程
        if(isReceivingData.compareAndSet(false,true)){
            //启动前台服务
            Notification.Builder builder=new Notification.Builder(this);
            Notification notice=builder
                    .setSmallIcon(R.drawable.ic_receiving_radio)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_radio_signal))
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.deciphering_gps_data))
                    .build();
            startForeground(123,notice);

            //打开串口
            String serialPortPath;
            int serialPortBdRate;

            try {
                serialPortPath = intent.getStringExtra(StaticData.SERIAL_PORT_PATH);
                serialPortBdRate = intent.getIntExtra(StaticData.SERIAL_PORT_BAUD_RATE, 115200);
                mSerialPort=new SerialPort(new File(serialPortPath), serialPortBdRate,0);
            } catch (Exception e) {
                stopSelf();
                BaseApplication.showToast("无法打开该串口，请核实串口路径及权限。");
                return super.onStartCommand(intent, flags, startId);
            }

            //获取流，并启动子线程接收数据
            mInputStream=mSerialPort.getInputStream();
            if(mInputStream!=null){
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isReceivingData.get()){
                            try {
                                //避免输入流堵塞
                                if(mInputStream.available()>0){
                                    //如果当前线程中断了，在耗时操作被执行前直接结束本线程
                                    if(mThread.isInterrupted()){
                                        break;
                                    }
                                    int len = mInputStream.read(temp);
                                    //把串口数据广播出去
                                    if(len>0){
                                        Intent it=new Intent(StaticData.ACTION_GPS_SERIAL_PORT_DATA);
                                        it.putExtra(StaticData.EXTRA_BYTES_GPS_MODULE_SERIAL_PORT_DATA, Arrays.copyOf(temp,len));
                                        sendBroadcast(it);
                                    }
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        break;
                                    }
                                }
                            } catch (IOException paramE) {
                                break;
                            }
                        }
                        if(mInputStream!=null){
                            try {
                                mInputStream.close();
                            } catch (IOException paramE) {
                                paramE.printStackTrace();
                            }
                        }
                        if(mSerialPort!=null){
                            mSerialPort.close();
                            mSerialPort=null;
                        }
                        stopSelf();
                    }
                });
                mThread.start();
            }else {
                BaseApplication.showToast("无法获取串口输入流。");
                stopSelf();
            }
        }else {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        isReceivingData.set(false);
        if(mThread!=null){
            mThread.interrupt();
        }
        stopForeground(true);
        showLog("服务关闭了。");
        super.onDestroy();
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }
}
