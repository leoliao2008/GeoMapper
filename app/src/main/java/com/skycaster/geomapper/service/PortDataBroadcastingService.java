package com.skycaster.geomapper.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.receivers.PortDataReceiver;
import com.skycaster.geomapper.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import project.SerialPort.SerialPort;

/**
 * Created by 廖华凯 on 2017/6/14.
 */

public class PortDataBroadcastingService extends Service {
    private static SerialPort stcSerialPort;
    private InputStream mInputStream;
    private AtomicBoolean isReceivingData=new AtomicBoolean(false);
    private int bufferSize=512;
    private byte[] temp=new byte[bufferSize];

    public static void setSerialPort(SerialPort paramSerialPort){
        stcSerialPort=paramSerialPort;
    }
    public static SerialPort getSerialPort(){
        return stcSerialPort;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Notification.Builder builder=new Notification.Builder(this);
        Notification notice=builder
                .setSmallIcon(R.drawable.ic_receiving_radio)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_radio_signal))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.deciphering_port_data))
                .build();
        startForeground(123,notice);
//        LogUtil.showLog(getClass().getSimpleName(),"service start1.");
        if(stcSerialPort!=null&&isReceivingData.compareAndSet(false,true)){
//            LogUtil.showLog(getClass().getSimpleName(),"service start2.");
            mInputStream=stcSerialPort.getInputStream();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (stcSerialPort!=null&&isReceivingData.get()){
//                        LogUtil.showLog(getClass().getSimpleName(),"service start3.");
                        try {
                            if(mInputStream.available()>0){
                                int len = mInputStream.read(temp);
//                                LogUtil.showLog(getClass().getSimpleName(),"service start4. len="+len);
                                if(len>0){
                                    Intent it=new Intent(PortDataReceiver.ACTION);
                                    it.putExtra(PortDataReceiver.DATA, Arrays.copyOf(temp,len));
                                    sendBroadcast(it);
                                }
                            }
                        } catch (IOException paramE) {
                            LogUtil.showLog(getClass().getSimpleName(),"error");
//                            paramE.printStackTrace();
                        }
                    }
                    if(mInputStream!=null){
                        try {
                            mInputStream.close();
                        } catch (IOException paramE) {
                            paramE.printStackTrace();
                        }
                    }
//                    LogUtil.showLog(getClass().getSimpleName(),"service stop2");
                }
            }).start();
        }else {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtil.showLog(getClass().getSimpleName(),"service stop1");
        super.onDestroy();
        isReceivingData.compareAndSet(true,false);
        stopForeground(true);
        if(stcSerialPort!=null){
            stcSerialPort.close();
            stcSerialPort=null;
        }

    }
}
