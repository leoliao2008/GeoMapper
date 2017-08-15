package com.skycaster.geomapper.service;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.Constants;
import com.skycaster.geomapper.models.BlueToothClientModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by 廖华凯 on 2017/8/15.
 */

public class BluetoothService extends Service {

    private BluetoothServiceBinder mBinder;
    private BluetoothDevice mDevice;

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        showLog("onBind");
        BlueToothClientModel.Callback callback = intent.getParcelableExtra(Constants.EXTRA_BLUETOOTH_CLIENT_MODEL_CALLBACK);
        mBinder=new BluetoothServiceBinder(callback);
        mDevice=intent.getParcelableExtra(Constants.EXTRA_BLUETOOTH_DEVICE);

        Notification.Builder builder=new Notification.Builder(getApplicationContext());
        Notification notice=builder
                .setSmallIcon(R.drawable.ic_receiving_radio)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_radio_signal))
                .setContentTitle(getString(R.string.app_name))
                .setContentText("正在监听蓝牙数据......")
                .build();
        startForeground(Constants.BLUETOOTH_SERVICE_FOREGROUND_ID,notice);
        showLog("startForeground");

        new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothSocket bluetoothSocket = BaseApplication.getBluetoothSocket();
                InputStream inputStream = null;
                try {
                    inputStream = bluetoothSocket.getInputStream();
                } catch (IOException e) {
                    showLog("fail to get input stream :"+e.getMessage());
                    stopForeground(true);
                    stopSelf();
                    mBinder.onBluetoothSocketFailsConnection(bluetoothSocket);
                    return;
                }

                byte[] temp=new byte[1024];
                int len;
                while (true){
                    try {
                        showLog("reading ......");
                        len = inputStream.read(temp);
                        if(len>0){
                            byte[] data = Arrays.copyOf(temp, len);
                            mBinder.onDataObtained(data,len);
                            Intent i=new Intent(Constants.ACTION_RECEIVE_BLUETOOTH_DATA);
                            i.putExtra(Constants.EXTRA_BLUETOOTH_DATA,data);
                            LocalBroadcastManager.getInstance(BaseApplication.getContext()).sendBroadcast(i);
                        }
                    } catch (IOException e) {
                        showLog("socket close :"+e.getMessage());
                        stopForeground(true);
                        stopSelf();
                        mBinder.onDeviceDisconnect(mDevice);
                        return;
                    }
                }
            }
        }).start();

        return mBinder;
    }


    public class BluetoothServiceBinder extends Binder{
        private BlueToothClientModel.Callback mCallback;

        private BluetoothServiceBinder(BlueToothClientModel.Callback callback) {
            mCallback = callback;
        }

        private void onBluetoothSocketFailsConnection(BluetoothSocket socket) {
            mCallback.onBluetoothSocketFailsConnection(socket);
        }

        private void onDataObtained(byte[] data,int len){
            mCallback.onDataObtained(data,len);
        }
        private void onDeviceDisconnect(BluetoothDevice device){
            mCallback.onDeviceDisconnect(device);
        }
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }
}
