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
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.service.BluetoothService;

import java.io.IOException;
import java.io.OutputStream;
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
    private BluetoothServiceReceiver mBluetoothServiceReceiver;

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

    public void requestStartGpgga(final OutputStream outputStream) throws IOException {
//        String request="\r\n+log gpgga ontime 1+\r\n";
//        outputStream.write(request.getBytes());
        outputStream.write(StaticData.STOP_SENDING_REGULAR_MESSAGES);
        BaseApplication.postDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    outputStream.write(StaticData.SEND_GPGGA_MESSAGE_ONLY);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },1000);

    }



    public void requestStopGpgga(OutputStream outputStream) throws IOException {
        String request="\r\n+unlog gpgga+\r\n";
        outputStream.write(request.getBytes());
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
                            mCallback.onFailToInitBluetoothSocket(device);
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
                        mCallback.onBluetoothSocketConnectSuccess(device,mSocket);
                    }
                });
            }
        }).start();
    }


    public void handleBluetoothCommunication(Context context,final BluetoothDevice device){
        //先停掉蓝牙服务（如有）
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(StaticData.ACTION_STOP_BLUETOOTH_SERVICE));
        //重新启动蓝牙服务
        Intent intent=new Intent(context,BluetoothService.class);
        intent.putExtra(StaticData.EXTRA_BLUETOOTH_DEVICE,device);
        context.startService(intent);
        showLog("start tooth service");
    }

    public void registerBluetoothServiceReceiver(Context context){
        mBluetoothServiceReceiver=new BluetoothServiceReceiver();
        IntentFilter intentFilter=new IntentFilter(StaticData.ACTION_RECEIVE_BLUETOOTH_DATA);
        LocalBroadcastManager.getInstance(context).registerReceiver(mBluetoothServiceReceiver,intentFilter);
    }

    public void unRegisterBluetoothServiceReceiver(Context context){
        if(mBluetoothServiceReceiver!=null){
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mBluetoothServiceReceiver);
            mBluetoothServiceReceiver=null;
        }
    }

    private class BluetoothServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device=intent.getParcelableExtra(StaticData.EXTRA_BLUETOOTH_DEVICE);
            int state=intent.getIntExtra(StaticData.EXTRA_BLUETOOTH_STATE,0);
            switch (state){
                case StaticData.EXTRA_BLUETOOTH_STATE_SOCKET_FAIL_TO_CONNECT:
                    mCallback.onBluetoothSocketFailsConnection(mSocket);
                    break;
                case StaticData.EXTRA_BLUETOOTH_STATE_DATA_SUCCESS:
                    byte[] data = intent.getByteArrayExtra(StaticData.EXTRA_BLUETOOTH_DATA);
                    mCallback.onDataObtained(data,data.length);
                    break;
                case StaticData.EXTRA_BLUETOOTH_STATE_DISCONNECT:
                    mCallback.onBluetoothSocketClose(device);
                    break;
                default:
                    break;
            }
        }
    }

    public void disconnectDevice(BluetoothSocket socket) throws IOException {
        if(socket.isConnected()){
            socket.close();
        }
    }

    public interface Callback {
        void onBluetoothStateChange(int preState, int newState);
        void onStartDiscoveringDevices();
        void onDeviceDiscovered(BluetoothClass bluetoothClass, BluetoothDevice device);
        void onCancelDiscoveringDevices();
        void onStartConnectingDevice(BluetoothDevice device);
        void onFailToInitBluetoothSocket(BluetoothDevice device);
        void onGetBluetoothSocket(BluetoothSocket socket);
        void onBluetoothSocketFailsConnection(BluetoothSocket socket);
        void onBluetoothSocketConnectSuccess(BluetoothDevice device, BluetoothSocket socket);
        void onBluetoothSocketClose(BluetoothDevice device);
        void onDataObtained(byte[] data, int dataLen);
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }






}
