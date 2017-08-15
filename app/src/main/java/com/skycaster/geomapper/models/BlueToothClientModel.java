package com.skycaster.geomapper.models;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.skycaster.geomapper.data.Constants;
import com.skycaster.geomapper.service.BluetoothService;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.EXTRA_PREVIOUS_STATE;
import static android.bluetooth.BluetoothAdapter.EXTRA_STATE;

/**
 * Created by 廖华凯 on 2017/8/10.
 */


/**
 * 教程 https://developer.android.google.cn/guide/topics/connectivity/bluetooth.html
 */
public class BlueToothClientModel extends BaseBluetoothModel {


    private BluetoothStateChangeReceiver mBluetoothStateChangeReceiver;
    private Callback mCallback;
    private BluetoothDeviceDiscoverReceiver mDeviceDiscoverReceiver;
    private Handler mHandler;
    private BluetoothSocket mSocket;
    private BluetoothService.BluetoothServiceBinder mBluetoothServiceBinder;

    public BlueToothClientModel(Callback callback) {
        mCallback = callback;
        mHandler=new Handler(Looper.getMainLooper());
    }

    public void registerBluetoothStateChangeReceiver(Activity activity){
        IntentFilter filter=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mBluetoothStateChangeReceiver=new BluetoothStateChangeReceiver();
        activity.registerReceiver(mBluetoothStateChangeReceiver,filter);
    }

    public void unRegisterBluetoothStateChangeReceiver(Activity activity){
        if(mBluetoothStateChangeReceiver!=null){
            activity.unregisterReceiver(mBluetoothStateChangeReceiver);
            mBluetoothStateChangeReceiver=null;
        }
    }

    private class BluetoothStateChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int newState = intent.getIntExtra(EXTRA_STATE,-99);
            int preState = intent.getIntExtra(EXTRA_PREVIOUS_STATE,-99);
            mCallback.onBluetoothStateChange(preState,newState);
        }
    }

    public Set<BluetoothDevice> getBondedDevices(Activity activity){
        return getBluetoothAdapter(activity).getBondedDevices();
    }

    public boolean startDiscoveringDevice(Activity activity){
        boolean isSuccess = getBluetoothAdapter(activity).startDiscovery();
        if(isSuccess){
            mCallback.onStartDiscoveringDevices();
        }
        return isSuccess;
    }

    public boolean cancelDiscoveringDevice(Activity activity){
        mCallback.onCancelDiscoveringDevices();
        return getBluetoothAdapter(activity).cancelDiscovery();
    }

    public void registerDiscoverReceiver(Activity activity){
        IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mDeviceDiscoverReceiver = new BluetoothDeviceDiscoverReceiver();
        activity.registerReceiver(mDeviceDiscoverReceiver,intentFilter);
    }

    public void unRegisterDiscoverReceiver(Activity activity){
        if(mDeviceDiscoverReceiver!=null){
            activity.unregisterReceiver(mDeviceDiscoverReceiver);
            mDeviceDiscoverReceiver=null;
        }
    }

    private class BluetoothDeviceDiscoverReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            showLog(action);
            if(!TextUtils.isEmpty(action)&& BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothClass bluetoothClass =intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mCallback.onDeviceDiscovered(bluetoothClass,device);
            }
        }
    }

    public void connectToServer(final BluetoothDevice device, final UUID uuid){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showLog("start to get blue tooth socket.");
                            mCallback.onStartConnectingDevice(device);
                        }
                    });
                    mSocket = device.createRfcommSocketToServiceRecord(uuid);
                    showLog("blue tooth socket got.");
                } catch (IOException e) {
                    showLog("blue tooth socket fails to get :"+e.getMessage());
                    try {
                        mSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.onFailToConnectDevice(device);
                        }
                    });
                    return;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onGetBluetoothSocket(mSocket);
                    }
                });

                try {
                    showLog("try to connect blue tooth socket...");
                    mSocket.connect();
                } catch (IOException e) {
                    showLog("blue tooth socket fails to connect :"+e.getMessage());
                    try {
                        mSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.onBluetoothSocketFailsConnection(mSocket);
                        }
                    });
                    return;
                }
                showLog("blue tooth socket connect success!");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onDeviceConnect(device,mSocket);
                    }
                });
            }
        }).start();
    }


    public void handleBluetoothCommunication(Context context,final BluetoothDevice device){
//        final byte[] buffer=new byte[1024];
//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                InputStream in;
//                try {
//                    in=socket.getInputStream();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    try {
//                        socket.close();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                    mCallback.onDeviceDisconnect(device);
//                    return;
//                }
//                while (true){
//                    try {
//                        final int readCount = in.read(buffer);
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                mCallback.onDataObtained(Arrays.copyOf(buffer,readCount),readCount);
//                            }
//                        });
//                    } catch (IOException e) {
//                        showLog("socket is closed.");
//                        e.printStackTrace();
//                        mCallback.onDeviceDisconnect(device);
//                        break;
//                    }
//                }
//            }
//        }).start();
        Intent intent=new Intent(context,BluetoothService.class);
        intent.putExtra(Constants.EXTRA_BLUETOOTH_CLIENT_MODEL_CALLBACK,mCallback);
        intent.putExtra(Constants.EXTRA_BLUETOOTH_DEVICE,device);
        ServiceConnection con=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBluetoothServiceBinder = (BluetoothService.BluetoothServiceBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        showLog("begin to bind blue tooth service");
        context.bindService(intent,con,Context.BIND_AUTO_CREATE);
        showLog("bind blue tooth service");

    }

    public void disconnectDevice(BluetoothSocket socket) throws IOException {
        if(socket.isConnected()){
            socket.close();
        }
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }

    public interface Callback {
        void onBluetoothStateChange(int preState, int newState);
        void onStartDiscoveringDevices();
        void onDeviceDiscovered(BluetoothClass bluetoothClass, BluetoothDevice device);
        void onCancelDiscoveringDevices();
        void onStartConnectingDevice(BluetoothDevice device);
        void onFailToConnectDevice(BluetoothDevice device);
        void onGetBluetoothSocket(BluetoothSocket socket);
        void onBluetoothSocketFailsConnection(BluetoothSocket socket);
        void onDeviceConnect(BluetoothDevice device, BluetoothSocket socket);
        void onDeviceDisconnect(BluetoothDevice device);
        void onDataObtained(byte[] data, int dataLen);
    }



}
