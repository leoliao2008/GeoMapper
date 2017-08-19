package com.skycaster.geomapper.service;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.StaticData;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by 廖华凯 on 2017/8/15.
 */

public class BluetoothService extends Service {

    private BluetoothDevice mDevice;
    private AtomicBoolean isLooping =new AtomicBoolean(false);
    private StopCommandReceiver mStopCommandReceiver;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Parcelable extra = intent.getParcelableExtra(StaticData.EXTRA_BLUETOOTH_DEVICE);
        if(extra!=null){
            mDevice= (BluetoothDevice) extra;
        }

        mStopCommandReceiver=new StopCommandReceiver();
        IntentFilter intentFilter=new IntentFilter(StaticData.ACTION_STOP_BLUETOOTH_SERVICE);
        registerReceiver(mStopCommandReceiver,intentFilter);

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
                } catch (IOException e) {
                    Intent i=new Intent(StaticData.ACTION_RECEIVE_BLUETOOTH_DATA);
                    i.putExtra(StaticData.EXTRA_BLUETOOTH_STATE, StaticData.EXTRA_BLUETOOTH_STATE_SOCKET_FAIL_TO_CONNECT);
                    i.putExtra(StaticData.EXTRA_BLUETOOTH_DEVICE,mDevice);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                    BaseApplication.post(new Runnable() {
                        @Override
                        public void run() {
                            BaseApplication.showToast("无法通过蓝牙端口连接上蓝牙服务端。");
                        }
                    });
                    stopForeground(true);
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
                            i.putExtra(StaticData.EXTRA_BLUETOOTH_DEVICE,mDevice);
                            LocalBroadcastManager.getInstance(BaseApplication.getContext()).sendBroadcast(i);
                        }
                    } catch (IOException e) {
                        showLog("socket close :"+e.getMessage());
                        Intent i=new Intent(StaticData.ACTION_RECEIVE_BLUETOOTH_DATA);
                        i.putExtra(StaticData.EXTRA_BLUETOOTH_STATE, StaticData.EXTRA_BLUETOOTH_STATE_DISCONNECT);
                        i.putExtra(StaticData.EXTRA_BLUETOOTH_DEVICE,mDevice);
                        LocalBroadcastManager.getInstance(BaseApplication.getContext()).sendBroadcast(i);
                        isLooping.compareAndSet(true,false);
                        BaseApplication.post(new Runnable() {
                            @Override
                            public void run() {
                                BaseApplication.showToast("蓝牙服务端断开了连接。");
                            }
                        });
                    }
                }
                stopForeground(true);
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
        //防止因未可预知的原因造成服务销毁，导致isLooping的值还没来得及变成false，所以在这里再设一次。
        isLooping.compareAndSet(true,false);

        if(mStopCommandReceiver!=null){
            unregisterReceiver(mStopCommandReceiver);
        }

        super.onDestroy();
    }

    private class StopCommandReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            isLooping.compareAndSet(true,false);
        }
    }


    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }
}
