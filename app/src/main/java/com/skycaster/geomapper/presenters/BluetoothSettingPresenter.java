package com.skycaster.geomapper.presenters;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Parcel;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.skycaster.geomapper.activity.BluetoothSettingActivity;
import com.skycaster.geomapper.adapter.BluetoothSearchResultAdapter;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.Constants;
import com.skycaster.geomapper.models.BlueToothClientModel;
import com.skycaster.geomapper.models.BlueToothClientModel.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by 廖华凯 on 2017/8/15.
 */

public class BluetoothSettingPresenter {

    private BluetoothSettingActivity mActivity;
    private BlueToothClientModel mClientModel;
    private ListView lstv_deviceList;
    private ListView lstv_dataList;
    private BluetoothSearchResultAdapter mSearchResultAdapter;
    private ArrayAdapter<String> mDataListAdapter;
    private ArrayList<BluetoothDevice> arr_deviceList =new ArrayList<>();
    private ArrayList<String> arr_dataList=new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private Runnable mRunnableCancelDiscovering;

    public BluetoothSettingPresenter(BluetoothSettingActivity activity) {
        mActivity = activity;
        lstv_deviceList =mActivity.getDeviceList();
        lstv_dataList=mActivity.getDataList();
        mRunnableCancelDiscovering=new Runnable() {
            @Override
            public void run() {
                mClientModel.cancelDiscoveringDevice(mActivity);
            }
        };
    }

    public void initData(){

        mSearchResultAdapter =new BluetoothSearchResultAdapter(arr_deviceList, mActivity);
        lstv_deviceList.setAdapter(mSearchResultAdapter);
        lstv_deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mClientModel.connectToServer(arr_deviceList.get(position), UUID.fromString(Constants.UUID));
            }
        });

        mDataListAdapter=new ArrayAdapter<String>(
                mActivity,
                android.R.layout.simple_list_item_1,
                arr_dataList
        );
        lstv_dataList.setAdapter(mDataListAdapter);




        mClientModel=new BlueToothClientModel(new Callback() {

            @Override
            public void onBluetoothStateChange(int preState, int newState) {
                if(preState==BluetoothAdapter.STATE_ON&&newState==BluetoothAdapter.STATE_TURNING_OFF){
                    BaseApplication.showToast("蓝牙被关闭。");
                    mClientModel.cancelDiscoveringDevice(mActivity);
                    BaseApplication.removeCallBack(mRunnableCancelDiscovering);
                    if(BaseApplication.getBluetoothSocket()!=null){
                        try {
                            mClientModel.disconnectDevice(BaseApplication.getBluetoothSocket());
                            BaseApplication.setBluetoothSocket(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onStartDiscoveringDevices() {
                mProgressDialog = ProgressDialog.show(
                        mActivity,
                        "蓝牙搜索",
                        "正在扫描附近蓝牙设备，请稍后......",
                        true,
                        true,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                mClientModel.cancelDiscoveringDevice(mActivity);
                                BaseApplication.removeCallBack(mRunnableCancelDiscovering);
                                BaseApplication.showToast("你取消了扫描设备。");
                            }
                        }
                );

            }

            @Override
            public void onDeviceDiscovered(BluetoothClass bluetoothClass, BluetoothDevice device) {
                if(!isDeviceDuplicate(device)){
                    addNewSearchResult(device);
                }
            }

            @Override
            public void onCancelDiscoveringDevices() {
                if(mProgressDialog!=null){
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onStartConnectingDevice(BluetoothDevice device) {
                mProgressDialog=ProgressDialog.show(
                        mActivity,
                        "连接设备",
                        "正在连接蓝牙设备，请稍候......",
                        true,
                        false
                );

            }

            @Override
            public void onFailToConnectDevice(BluetoothDevice device) {
                if(mProgressDialog!=null){
                    mProgressDialog.dismiss();
                }
                BaseApplication.showToast("连接设备失败。");
            }

            @Override
            public void onGetBluetoothSocket(BluetoothSocket socket) {
                BaseApplication.setBluetoothSocket(socket);
            }

            @Override
            public void onBluetoothSocketFailsConnection(BluetoothSocket socket) {
                BaseApplication.showToast("连接设备失败。");

            }

            @Override
            public void onDeviceConnect(BluetoothDevice device, BluetoothSocket socket) {
                if(mProgressDialog!=null){
                    mProgressDialog.dismiss();
                }
                BaseApplication.showToast("连接设备成功！");
                mClientModel.handleBluetoothCommunication(mActivity,device);
            }

            @Override
            public void onDeviceDisconnect(BluetoothDevice device) {
                BaseApplication.showToast("设备已断开。");
            }

            @Override
            public void onDataObtained(byte[] data, int dataLen) {
                String s=new String(data,0,dataLen);
                updateDataList(s);
            }
        });

        registerReceivers();
    }

    private boolean isDeviceDuplicate(BluetoothDevice device) {
        for(int i = 0, len = arr_deviceList.size(); i<len; i++){
            BluetoothDevice temp = arr_deviceList.get(i);
            if(temp.getAddress().equals(device.getAddress())){
                return true;
            }
        }
        return false;
    }

    private void registerReceivers(){
        mClientModel.registerBluetoothStateChangeReceiver(mActivity);
        mClientModel.registerDiscoverReceiver(mActivity);
    }

    private void unRegisterReceivers(){
        mClientModel.unRegisterBluetoothStateChangeReceiver(mActivity);
        mClientModel.unRegisterDiscoverReceiver(mActivity);
    }

    public void searchDevices(){
        arr_deviceList.clear();
        Set<BluetoothDevice> bondedDevices = mClientModel.getBondedDevices(mActivity);
        arr_deviceList.addAll(bondedDevices);
        boolean isSuccess = mClientModel.startDiscoveringDevice(mActivity);
        if(isSuccess){
            BaseApplication.postDelay(new Runnable() {
                @Override
                public void run() {
                    mClientModel.cancelDiscoveringDevice(mActivity);
                }
            },12000);
        }
    }

    public void addNewSearchResult(BluetoothDevice result){
        if(arr_deviceList.contains(result)){
            return;
        }
        arr_deviceList.add(result);
        mSearchResultAdapter.notifyDataSetChanged();
        lstv_deviceList.smoothScrollToPosition(Integer.MAX_VALUE);
    }

    public void updateDataList(String newData){
        arr_dataList.add(newData);
        mDataListAdapter.notifyDataSetChanged();
        lstv_dataList.smoothScrollToPosition(Integer.MAX_VALUE);
    }

    public void onResume(){
        if(!mClientModel.checkIfBluetoothAvailable(mActivity)){
            mClientModel.requestEnableBluetooth(mActivity);
        }
    }

    public void onDestroy(){
        unRegisterReceivers();
    }

    public void onActivityResult(int requestCode,int resultCode){
        mClientModel.onRequestEnableBluetooth(requestCode, resultCode,
                new Runnable() {
                    @Override
                    public void run() {
                        BaseApplication.showToast("蓝牙已经开启。");
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        BaseApplication.showToast("蓝牙启动失败,即将退出此页面。");
                        mActivity.onBackPressed();
                    }
                });
    }


}
