package com.skycaster.geomapper.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.StaticData;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 廖华凯 on 2017/8/19.
 */

public class ReconnectBtDeviceService extends Service {
    private BluetoothSocket mSocket;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BluetoothDevice device = BaseApplication.getBluetoothDevice();
        if(device!=null){
            connectToServer(device,UUID.fromString(StaticData.UUID));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void connectToServer(final BluetoothDevice device, final UUID uuid){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BaseApplication.setBluetoothDevice(null);
                    BaseApplication.setBluetoothSocket(null);
                    mSocket = device.createRfcommSocketToServiceRecord(uuid);
                } catch (IOException e) {
                    try {
                        mSocket.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    stopSelf();
                    return;
                }
                try {
                    showLog("try to connect blue tooth socket...");
                    mSocket.connect();
                } catch (Exception e) {
                    showLog("blue tooth socket fails to connect :"+e.getMessage());
                    try {
                        mSocket.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    stopSelf();
                    return;
                }
                showLog("blue tooth socket connect success!");
                //连接成功，更新当前连接蓝牙设备
                BaseApplication.setBluetoothDevice(device);
                BaseApplication.setBluetoothSocket(mSocket);

                Intent intent = new Intent(ReconnectBtDeviceService.this, BluetoothService.class);
                startService(intent);
                showLog("start service complete.");
                stopSelf();
            }
        }).start();
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }
}
