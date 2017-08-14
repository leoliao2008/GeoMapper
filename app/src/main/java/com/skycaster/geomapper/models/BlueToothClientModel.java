package com.skycaster.geomapper.models;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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

    public void connectToServer(final BluetoothDevice server, final UUID uuid){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mCallback.onStartConnectingDevice(server);
                    mSocket = server.createRfcommSocketToServiceRecord(uuid);
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        mSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    mCallback.onFailToConnectDevice(server);
                    return;
                }
                try {
                    mSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        mSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    mCallback.onFailToConnectDevice(server);
                    return;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onDeviceConnected(server,mSocket);
                    }
                });
            }
        }).start();
    }


    public void handleBluetoothCommunication(final BluetoothDevice device, final BluetoothSocket socket){
        final byte[] buffer=new byte[1024];
        new Thread(new Runnable(){
            @Override
            public void run() {
                InputStream in;
                try {
                    in=socket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    mCallback.onDisconnectDevice(device);
                    return;
                }
                while (true){
                    try {
                        final int readCount = in.read(buffer);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mCallback.onDataObtained(Arrays.copyOf(buffer,readCount),readCount);
                            }
                        });
                    } catch (IOException e) {
                        showLog("socket is closed.");
                        e.printStackTrace();
                        mCallback.onDisconnectDevice(device);
                        break;
                    }
                }
            }
        }).start();
    }

    public void disconnectDevice(BluetoothSocket socket) throws IOException {
        socket.close();
    }

    public void disconnectDevice() throws IOException {
        if(mSocket!=null){
            mSocket.close();
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
        void onDeviceConnected(BluetoothDevice device, BluetoothSocket socket);
        void onDisconnectDevice(BluetoothDevice device);
        void onDataObtained(byte[] data, int dataLen);
    }


}
