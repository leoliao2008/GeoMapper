package com.skycaster.geomapper.models;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.StaticData;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 廖华凯 on 2017/8/14.
 */

public class BaseBluetoothModel implements iModel {
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothManager getBluetoothManager(Activity activity){
        if(mBluetoothManager==null){
            mBluetoothManager=(BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        }
        return mBluetoothManager;
    }

    public BluetoothAdapter getBluetoothAdapter(Activity activity){
        if(mBluetoothAdapter==null){
            mBluetoothAdapter = getBluetoothManager(activity).getAdapter();
        }
        return mBluetoothAdapter;
    }

    public boolean checkIfBluetoothAvailable(Activity activity){
        if(!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            activity.onBackPressed();
            BaseApplication.showToast("设备无蓝牙功能，退出本页面。");
            return false;
        }
        if(getBluetoothManager(activity) ==null){
            activity.onBackPressed();
            BaseApplication.showToast("设备无蓝牙功能，退出本页面。");
            return false;
        }
        if(getBluetoothAdapter(activity)==null){
            activity.onBackPressed();
            BaseApplication.showToast("设备无蓝牙功能，退出本页面。");
            return false;
        }
        return getBluetoothAdapter(activity).isEnabled();
    }

    public void requestEnableBluetooth(Activity activity){
        Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, StaticData.REQUEST_CODE_ENABLE_BLUETOOTH);
    }

    public void onRequestEnableBluetooth(int requestCode, int resultCode, Runnable onGranted, Runnable onDenied){
        if(requestCode== StaticData.REQUEST_CODE_ENABLE_BLUETOOTH){
            if(resultCode==RESULT_OK){
                onGranted.run();
            }else {
                onDenied.run();
            }
        }
    }

    @Override
    public void onDetachFromPresenter() {

    }
}
