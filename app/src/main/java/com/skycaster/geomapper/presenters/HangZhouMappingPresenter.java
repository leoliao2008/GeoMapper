package com.skycaster.geomapper.presenters;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.text.TextUtils;
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
import com.skycaster.geomapper.activity.HangZhouMappingActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.customized.MapTypeSelector;
import com.skycaster.geomapper.models.BaiduMapModel;
import com.skycaster.geomapper.models.GPIOModel;
import com.skycaster.geomapper.receivers.PortDataReceiver;
import com.skycaster.geomapper.util.AlertDialogUtil;
import com.skycaster.inertial_navi_lib.GPGGABean;
import com.skycaster.inertial_navi_lib.NaviDataExtractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 廖华凯 on 2017/10/23.
 */

public class HangZhouMappingPresenter {
    private HangZhouMappingActivity mActivity;
    private MapTypeSelector mMapTypeSelector;
    private TextureMapView mMapView;
    private BaiduMapModel mMapModel;
    private float mZoomLevel=21;
    private float mRotate=0;
    private GPGGABean mGPGGABean;
    private AtomicBoolean isFirstTimeGetLocation=new AtomicBoolean(true);
    private ArrayList<LatLng> mMyLocations=new ArrayList<>();
    private Runnable mRunnableUpdateMyLocation=new Runnable() {
        @Override
        public void run() {
            if(mGPGGABean==null){
                return;
            }
            final Location location = mGPGGABean.getLocation();
            //更新textSwitcher
            mActivity.getTextSwitcher().setText(mGPGGABean.getRawGpggaString());
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
    private MyPortDataReceiver mPortDataReceiver;
    private GPIOModel mGPIOModel;//通过操纵GPIO控制CDRadio模块串口的切换

    public HangZhouMappingPresenter(HangZhouMappingActivity activity) {
        mActivity = activity;
    }

    public void init(){
        mMapTypeSelector=mActivity.getMapTypeSelector();
        mMapView=mActivity.getMapView();
        mMapModel=new BaiduMapModel();
        mGPIOModel=new GPIOModel();
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

    public void onResume(){
        mMapView.onResume();
    }

    public void onPause(){
        mMapView.onPause();
    }

    public void onStart(){
        //启动CDRadio模块
        try {
            mGPIOModel.turnOnCdRadio();
        } catch (IOException e) {
            handleException(e);
        }
        registerReceivers();
    }

    private void handleException(Exception e) {
        String message = e.getMessage();
        if(TextUtils.isEmpty(message)){
            message="Exception Unknown.";
        }
        mActivity.showHint(message);
    }

    private void registerReceivers() {
        mPortDataReceiver = new MyPortDataReceiver();
        IntentFilter intentFilter=new IntentFilter(PortDataReceiver.ACTION);
        mActivity.registerReceiver(mPortDataReceiver,intentFilter);
    }

    public void onStop(){
        unRegisterReceivers();
        BaseApplication.removeCallBacks(mRunnableUpdateMyLocation);
        //关闭CDRadio模块
        try {
            mGPIOModel.turnOffCdRadio();
        } catch (IOException e) {
            handleException(e);
        }
    }

    private void unRegisterReceivers() {
        if(mPortDataReceiver!=null){
            mActivity.unregisterReceiver(mPortDataReceiver);
            mPortDataReceiver=null;
        }
    }

    public void onDestroy(){
        mMapView.onDestroy();
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }

    public void startLocService() {
        //启动21489》设置主频、左频、右频(跳过这一步，这一步事先在“系统设置”中设置好。)》启动业务》GPIO切换串口
        if(!mActivity.isServiceRunning()){
            try {
                //把串口切回来
                mGPIOModel.connectCdRadioToCPU();
            } catch (IOException e) {
                handleException(e);
            }
            //启动21489
            mActivity.getRequestManager().activate(true);
        }

    }

    public void stopLocService() {
        if(mActivity.isServiceRunning()){
            try {
                //把串口切回来
                mGPIOModel.connectCdRadioToCPU();
            } catch (IOException e) {
                handleException(e);
            }
            //关闭21489
            mActivity.getRequestManager().activate(false);
        }
    }

    public void connectCdRadioToBeidou() {
        try {
            mGPIOModel.connectCDRadioToBeidou();
        } catch (IOException e) {
            handleException(e);
        }
    }

    public class MyPortDataReceiver extends PortDataReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] bytes = intent.getByteArrayExtra(PortDataReceiver.DATA);
            NaviDataExtractor.decipherData(bytes, bytes.length,mCallBack);
        }
    }


}
