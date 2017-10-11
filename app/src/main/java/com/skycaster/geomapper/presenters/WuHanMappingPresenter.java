package com.skycaster.geomapper.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.WuhanMappingActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.customized.MapTypeSelector;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.models.BaiduMapModel;
import com.skycaster.geomapper.models.GpggaRecordModel;
import com.skycaster.geomapper.util.AlertDialogUtil;
import com.skycaster.inertial_navi_lib.GPGGABean;
import com.skycaster.inertial_navi_lib.NaviDataExtractor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 廖华凯 on 2017/8/15.
 */

public class WuHanMappingPresenter {
    private WuhanMappingActivity mActivity;
    private TextureMapView mMapView;
    private MapTypeSelector mMapTypeSelector;
    private BaiduMapModel mMapModel;
    private BluetoothServiceReceiver mBluetoothServiceReceiver;
    private AtomicBoolean isFirstTimeGetLocation=new AtomicBoolean(true);
    private ArrayList<LatLng> mMyLocations=new ArrayList<>();
    private float mZoomLevel=21;
    private float mRotate=0;
    private GPGGABean mGPGGABean;
    private File mGpggaRecord;
    private BufferedOutputStream mGpggaBos;
    private GpggaRecordModel mGpggaRecordModel;
    private Runnable mRunnableUpdateMyLocation=new Runnable() {
        @Override
        public void run() {
            if(mGPGGABean==null){
                return;
            }
            final Location location = mGPGGABean.getLocation();
            //更新textSwitcher
            //mActivity.getTextSwitcher().setText("Lat: "+String.format("%.13f",location.getLatitude())+" , Lng: "+String.format("%.13f",location.getLongitude()));
            mActivity.getTextSwitcher().setText(mGPGGABean.getRawGpggaString());
            //保存信息到本地
            if(mActivity.getIsAutoSaveGpggaData().get()){
                try {
                    mGpggaRecordModel.write(mGpggaBos,mGPGGABean.getRawGpggaString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //更新坐标位置
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            final BDLocation bdLocation = mMapModel.convertToBaiduCoord(latLng);
            updateMyLocation(bdLocation);
            //更新轨迹
            mMyLocations.add(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()));
            try {
                mMapModel.updateMovingTrace(mMapView, mMyLocations);
            } catch (BaiduMapModel.PositionCountsInvalidException e) {
                e.printStackTrace();
            }
            //更新小灯笼
            mActivity.getLanternView().updateLantern(mGPGGABean.getFixQuality());
        }
    };
    private NaviDataExtractor.CallBack mCallBack=new NaviDataExtractor.CallBack() {
        @Override
        public void onGetGPGGABean(GPGGABean gpggaBean) {
            mGPGGABean=gpggaBean;
            BaseApplication.post(mRunnableUpdateMyLocation);
        }
    };



    public WuHanMappingPresenter(WuhanMappingActivity activity) {
        mActivity = activity;
        mMapView=mActivity.getMapView();
        mMapTypeSelector=mActivity.getMapTypeSelector();
        mMapModel=new BaiduMapModel();
        mGpggaRecordModel =new GpggaRecordModel();
    }

    public void initData(){
        initTextSwitcher(mActivity.getTextSwitcher());
        mMapTypeSelector.attachToMapView(mMapView);
        mMapModel.initBaiduMap(mMapView);
        mMapModel.setOnMapStatusChangeListener(mMapView, new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                mZoomLevel =mapStatus.zoom;
                mRotate = mapStatus.rotate;
                showLog("onMapStatusChange : mZoomLevel = "+mZoomLevel+" ,mRotate = "+mRotate);
            }
        });

        registerReceivers();

    }

    public void createNewGpggaRecord(){
        stopRecordingGpgga();
        try {
            mGpggaRecord= mGpggaRecordModel.createDestFile(mActivity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(mGpggaRecord!=null&&mGpggaRecord.exists()){
            try {
                mGpggaBos=new BufferedOutputStream(new FileOutputStream(mGpggaRecord));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopRecordingGpgga(){
        if(mGpggaBos!=null){
            try {
                mGpggaBos.flush();
                mGpggaBos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mGpggaBos=null;
        }
        mGpggaRecord=null;
    }

    private void initTextSwitcher(final TextSwitcher textSwitcher) {
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView=new TextView(mActivity);
                textView.setTextColor(Color.RED);
                textView.setLayoutParams(new TextSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                textView.setPadding(5,5,5,5);
                textView.setGravity(Gravity.CENTER);
                return textView;
            }
        });
        textSwitcher.setInAnimation(mActivity, R.anim.anim_text_switcher_in);
        textSwitcher.setOutAnimation(mActivity,R.anim.anim_text_switcher_out);
    }

    private void updateMyLocation(BDLocation bdLocation){
        mMapModel.updateMyLocation(mMapView.getMap(),bdLocation);
        //第一次定位成功或者当前定位模式为导航模式，都会跳到最新位置上。
        if(isFirstTimeGetLocation.compareAndSet(true,false)){
            mMapModel.focusToLocation(mMapView.getMap(),bdLocation,0,21);
        }else if(mActivity.getIsInNaviMode().get()){
            mMapModel.focusToLocation(mMapView.getMap(),bdLocation,mRotate,mZoomLevel);
        }
    }

    public void onResume(){
        mMapView.onResume();
    }

    public void onPause(){
        mMapView.onPause();
    }

    public void onDestroy(){
        mMapView.onDestroy();
        unRegisterReceivers();
        BaseApplication.removeCallBacks(mRunnableUpdateMyLocation);
        stopRecordingGpgga();
    }

    private void registerReceivers(){
        mBluetoothServiceReceiver=new BluetoothServiceReceiver();
        IntentFilter intentFilter=new IntentFilter(StaticData.ACTION_RECEIVE_BLUETOOTH_DATA);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBluetoothServiceReceiver,intentFilter);
    }

    private void unRegisterReceivers(){
        if(mBluetoothServiceReceiver!=null){
            LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBluetoothServiceReceiver);
            mBluetoothServiceReceiver=null;
        }
    }

    public void confirmClearTrace() {
        AlertDialogUtil.showHint(
                mActivity,
                "您确定要清除之前的历史轨迹吗？",
                new Runnable() {
                    @Override
                    public void run() {
                        BaseApplication.post(new Runnable() {
                            @Override
                            public void run() {
                                BaseApplication.removeCallBacks(mRunnableUpdateMyLocation);
                                mMyLocations.clear();
                                mMapView.getMap().clear();
                                BaseApplication.showToast("历史轨迹已经清除。");
                            }
                        });
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        //do nothing
                    }
                }
        );

    }

    private class BluetoothServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getIntExtra(StaticData.EXTRA_BLUETOOTH_STATE,0)== StaticData.EXTRA_BLUETOOTH_STATE_DATA_SUCCESS){
                byte[] data = intent.getByteArrayExtra(StaticData.EXTRA_BLUETOOTH_DATA);
                NaviDataExtractor.decipherData(data,data.length,mCallBack);
            }
        }
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }
}
