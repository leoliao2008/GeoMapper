package com.skycaster.geomapper.service;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.StaticData;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by 廖华凯 on 2017/8/15.
 */

public class BluetoothService extends Service {

    private AtomicBoolean isLooping =new AtomicBoolean(false);


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Notification.Builder builder=new Notification.Builder(getApplicationContext());
        Notification notice=builder
                .setSmallIcon(R.drawable.ic_receiving_radio)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_radio_signal))
                .setContentTitle(getString(R.string.app_name))
                .setContentText("正在监听蓝牙数据......")
                .build();
        startForeground(StaticData.BLUETOOTH_SERVICE_FOREGROUND_ID,notice);
        showLog("startForeground");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                BluetoothSocket bluetoothSocket = BaseApplication.getBluetoothSocket();
                InputStream inputStream = null;
                try {
                    inputStream = bluetoothSocket.getInputStream();
                } catch (Exception e) {
                    Intent i=new Intent(StaticData.ACTION_RECEIVE_BLUETOOTH_DATA);
                    i.putExtra(StaticData.EXTRA_BLUETOOTH_STATE, StaticData.EXTRA_BLUETOOTH_STATE_SOCKET_FAIL_TO_CONNECT);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                    BaseApplication.post(new Runnable() {
                        @Override
                        public void run() {
                            BaseApplication.showToast("无法通过蓝牙端口连接上蓝牙服务端。");
                        }
                    });
                    stopSelf();
                    return;
                }

                byte[] temp=new byte[1024];
                int len;
                isLooping.compareAndSet(false,true);
                while (isLooping.get()){
                    try {
                        len = inputStream.read(temp);
                        if(len>0){
                            final byte[] data = Arrays.copyOf(temp, len);
                            showLog("data = "+new String(data,0,len));
                            Intent i=new Intent(StaticData.ACTION_RECEIVE_BLUETOOTH_DATA);
                            i.putExtra(StaticData.EXTRA_BLUETOOTH_STATE, StaticData.EXTRA_BLUETOOTH_STATE_DATA_SUCCESS);
                            i.putExtra(StaticData.EXTRA_BLUETOOTH_DATA,data);
                            LocalBroadcastManager.getInstance(BaseApplication.getContext()).sendBroadcast(i);
                        }
                    } catch (Exception e) {
                        if(BaseApplication.getBluetoothDevice()==null){
                            Intent i=new Intent(StaticData.ACTION_RECEIVE_BLUETOOTH_DATA);
                            i.putExtra(StaticData.EXTRA_BLUETOOTH_STATE, StaticData.EXTRA_BLUETOOTH_STATE_DISCONNECT);
                            LocalBroadcastManager.getInstance(BaseApplication.getContext()).sendBroadcast(i);
                            isLooping.compareAndSet(true,false);
                            BaseApplication.post(new Runnable() {
                                @Override
                                public void run() {
                                    BaseApplication.showToast("蓝牙服务端断开了连接。");
                                }
                            });
                        }else {
                            startService(new Intent(BluetoothService.this,ReconnectBtDeviceService.class));
                        }
                        break;
                    }
                }
                stopSelf();

            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isLooping.compareAndSet(true,false);
        stopForeground(true);
        super.onDestroy();
    }


    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }
}
