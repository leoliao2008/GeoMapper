package com.skycaster.geomapper.presenters;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.skycaster.geomapper.activity.BluetoothSettingActivity;
import com.skycaster.geomapper.adapter.BluetoothSearchResultAdapter;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.models.BlueToothClientModel;

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
    private Runnable mRunnableCancelDiscovering=new Runnable() {
        @Override
        public void run() {
            mClientModel.cancelDiscoveringDevice(mActivity);
        }
    };




    public BluetoothSettingPresenter(BluetoothSettingActivity activity) {
        mActivity = activity;
        lstv_deviceList =mActivity.getDeviceList();
        lstv_dataList=mActivity.getDataList();
    }

    private void initModels() {
        mClientModel=new BlueToothClientModel(new BlueToothClientModel.Callback() {

            @Override
            public void onBluetoothStateChange(int preState, int newState) {
                if(preState== BluetoothAdapter.STATE_ON&&newState==BluetoothAdapter.STATE_OFF){
                    BaseApplication.showToast("蓝牙被关闭。");
                    mClientModel.cancelDiscoveringDevice(mActivity);
                    //因为搜索已经取消，所以清除现有未执行的取消搜索的runnable
                    BaseApplication.removeCallBacks(mRunnableCancelDiscovering);

                    if(BaseApplication.getBluetoothSocket()!=null){
                        try {
                            mClientModel.disconnectDevice(BaseApplication.getBluetoothSocket());
                            BaseApplication.setBluetoothSocket(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    BaseApplication.post(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.getBtn_requestStart().setEnabled(false);
                        }
                    });
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

                                //因为搜索已经取消，所以清除现有未执行的取消搜索的runnable
                                BaseApplication.removeCallBacks(mRunnableCancelDiscovering);

                                BaseApplication.showToast("你取消了扫描设备。");
                            }
                        }
                );

            }

            @Override
            public void onDeviceDiscovered(BluetoothClass bluetoothClass, BluetoothDevice device) {
                if(!isDeviceDuplicate(device)){
                    showLog("device add to result list.");
                    addNewSearchResult(device);
                }else {
                    showLog("device duplicate. dump.");
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
            public void onFailToInitBluetoothSocket(BluetoothDevice device) {
                if(mProgressDialog!=null){
                    mProgressDialog.dismiss();
                }
                BaseApplication.showToast("连接设备失败。");
                mActivity.getBtn_requestStart().setEnabled(false);
            }

            @Override
            public void onGetBluetoothSocket(BluetoothSocket socket) {
            }

            @Override
            public void onBluetoothSocketFailsConnection(BluetoothSocket socket) {
                if(mProgressDialog!=null){
                    mProgressDialog.dismiss();
                }
                BaseApplication.showToast("连接设备失败。");
                mActivity.getBtn_requestStart().setEnabled(false);

            }

            @Override
            public void onBluetoothSocketConnectSuccess(BluetoothDevice device, BluetoothSocket socket) {
                if(mProgressDialog!=null){
                    mProgressDialog.dismiss();
                }
                mActivity.getBtn_requestStart().setEnabled(true);
                BaseApplication.showToast("连接设备成功！");
            }

            @Override
            public void onBluetoothSocketClose(BluetoothDevice device) {
                mActivity.getBtn_requestStart().setEnabled(false);
                BaseApplication.showToast("设备连接断开。");
            }

            @Override
            public void onDataObtained(byte[] data, int dataLen) {
                String s=new String(data,0,dataLen);
                updateDataList(s);
            }
        });
    }

    public void initData(){
        //初始化启动按钮
        mActivity.getBtn_requestStart().setEnabled(false);


        //初始化功能模块
        initModels();


        //初始化蓝牙设备清单
        mSearchResultAdapter =new BluetoothSearchResultAdapter(arr_deviceList, mActivity);
        lstv_deviceList.setAdapter(mSearchResultAdapter);
        lstv_deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                BluetoothDevice targetDevice = arr_deviceList.get(position);
                mClientModel.connectToServer(targetDevice, UUID.fromString(StaticData.UUID));

            }
        });
        //如果之前在此页面已经连接上设备了，重新进入此页面，则把该设备显示出来。
        if(BaseApplication.getBluetoothDevice()!=null){
            arr_deviceList.add(BaseApplication.getBluetoothDevice());
            mSearchResultAdapter.notifyDataSetChanged();
        }


        //初始化蓝牙数据清单
        mDataListAdapter=new ArrayAdapter<String>(
                mActivity,
                android.R.layout.simple_list_item_1,
                arr_dataList
        );
        lstv_dataList.setAdapter(mDataListAdapter);

        //注册广播接收者
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
        mClientModel.registerBluetoothServiceReceiver(mActivity);
    }

    private void unRegisterReceivers(){
        mClientModel.unRegisterBluetoothStateChangeReceiver(mActivity);
        mClientModel.unRegisterDiscoverReceiver(mActivity);
        mClientModel.unRegisterBluetoothServiceReceiver(mActivity);
    }

    public void searchDevices(){
        arr_deviceList.clear();
        Set<BluetoothDevice> bondedDevices = mClientModel.getBondedDevices(mActivity);
        arr_deviceList.addAll(bondedDevices);
        mSearchResultAdapter.notifyDataSetChanged();
        boolean isSuccess = mClientModel.startDiscoveringDevice(mActivity);
        if(isSuccess){
            //12秒后停止搜索,这段时间内如果用户主动取消了搜索，记得也要从任务队列中把这个任务去掉。
            BaseApplication.postDelay(mRunnableCancelDiscovering,12000);
        }
    }

    private void addNewSearchResult(BluetoothDevice result){
        if(arr_deviceList.contains(result)){
            return;
        }
        arr_deviceList.add(result);
        mSearchResultAdapter.notifyDataSetChanged();
        lstv_deviceList.smoothScrollToPosition(Integer.MAX_VALUE);
    }

    private void updateDataList(String newData){
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
                        mActivity.finish();
                    }
                });
    }


    public void requestStartGpggaTransmission() {
        BluetoothSocket socket = BaseApplication.getBluetoothSocket();
        if(socket!=null){
            try {
                mClientModel.requestStartGpgga(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mClientModel.handleBluetoothCommunication(mActivity);
    }

    public void requestStopGpggaTransmission(){
        BluetoothSocket socket = BaseApplication.getBluetoothSocket();
        if(socket!=null){
            try {
                mClientModel.requestStopGpgga(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }
}
