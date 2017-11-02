package com.skycaster.geomapper.presenters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.MappingActivity;
import com.skycaster.geomapper.activity.SaveLocationActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.RouteRecordOpenHelper;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.interfaces.GetGeoInfoListener;
import com.skycaster.geomapper.interfaces.RouteRecordSelectedListener;
import com.skycaster.geomapper.models.BaiduMapModel;
import com.skycaster.geomapper.models.GPIOModel;
import com.skycaster.geomapper.util.AlertDialogUtil;
import com.skycaster.inertial_navi_lib.NaviDataExtractor;
import com.skycaster.inertial_navi_lib.NaviDataExtractorCallBack;
import com.skycaster.inertial_navi_lib.TbGNGGABean;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/11/1.
 */

public class MappingActivityPresenter {

    private MappingActivity mActivity;
    private BaiduMapModel mBaiduMapModel;
    private TextureMapView mMapView;
    private float mTextSize;
    private Overlay mOverlay;
    private TbGNGGABean mTbGNGGABean;
    private BeidouPortDataReceiver mBeidouPortDataReceiver;
    private NaviDataExtractorCallBack mDataExtractorCallBack = new NaviDataExtractorCallBack() {
        @Override
        public void onGetTBGNGGABean(TbGNGGABean tbGNGGABean) {
            mTbGNGGABean=tbGNGGABean;
            BaseApplication.post(mRunnableOnGetTBGNGGA);

        }
    };
    private Runnable mRunnableOnGetTBGNGGA =new Runnable() {
        @Override
        public void run() {
            //更新小灯笼
            mActivity.getLanternView().updateLantern(mTbGNGGABean.getFixQuality());
            //在导航模式下，更新当前位置
            if(mActivity.isInNaviMode()){
                BDLocation bdLocation = mBaiduMapModel.convertToBaiduCoord(new LatLng(mTbGNGGABean.getLocation().getLatitude(), mTbGNGGABean.getLocation().getLongitude()));
                mBaiduMapModel.updateMyLocation(mMapView.getMap(),bdLocation);
            }
            //更新底部的txt switcher
            mActivity.getTxtSwitcher().setText(mTbGNGGABean.getRawString());
            //// TODO: 2017/11/2 其他后续操作

        }
    };
    private GPIOModel mGpioModel;
    private Overlay mHistoryRouteOverlay;
    private ActionMode.Callback mActionModeCallback=new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };




    public MappingActivityPresenter(MappingActivity activity) {
        mActivity = activity;
    }

    public void init(){
        mGpioModel=new GPIOModel();
        mBaiduMapModel=new BaiduMapModel();
        mMapView=mActivity.getMapView();
        mBaiduMapModel.initBaiduMap(mMapView);
        mActivity.getMapTypeSelector().attachToMapView(mMapView);
        initActionBar();
        initTextSwitcher();
    }

    private void initTextSwitcher() {
        mTextSize = mActivity.getResources().getDimensionPixelSize(R.dimen.sp_15)/ BaseApplication.getDisplayMetrics().scaledDensity;
        mActivity.getTxtSwitcher().setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView=new TextView(mActivity);
                FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setLayoutParams(params);
                textView.setTextSize(mTextSize);
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundResource(R.drawable.shape_black_transparent_round);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });
        mActivity.getTxtSwitcher().setText("北斗模块正在启动，需要1-2分钟时间，请稍候...");

    }

    private void initActionBar() {
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(null);
        }
    }

    public void onStart() {
        mBeidouPortDataReceiver=new BeidouPortDataReceiver();
        IntentFilter intentFilter=new IntentFilter(StaticData.ACTION_SEND_BEIDOU_SP_DATA);
        mActivity.registerReceiver(mBeidouPortDataReceiver,intentFilter);
        activateCdRadio();

    }

    /**
     * 启动CdRadio模块
     */
    private void activateCdRadio() {
        try {
            mGpioModel.connectCdRadioToCPU();
            BaseApplication.postDelay(new Runnable() {
                @Override
                public void run() {
                    mActivity.getRequestManager().activate(true);
                }
            },500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onPause(){
        mMapView.onPause();
    }

    public void onResume(){
        mMapView.onResume();
    }

    public void onStop() {
        if(mBeidouPortDataReceiver!=null){
            mActivity.unregisterReceiver(mBeidouPortDataReceiver);
            mBeidouPortDataReceiver=null;
        }
        BaseApplication.removeCallBacks(mRunnableOnGetTBGNGGA);
        deactivateCdRadio();
    }

    /**
     * 停止CdRadio模块的所有业务
     */
    private void deactivateCdRadio() {
        try {
            mGpioModel.connectCdRadioToCPU();
            BaseApplication.postDelay(new Runnable() {
                @Override
                public void run() {
                    mActivity.getRequestManager().activate(false);
                }
            },500);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy(){
        mMapView.onDestroy();
    }

    public void saveCurrentLocation(){
        if(mTbGNGGABean==null){
            mActivity.showToast("当前无定位数据，请稍后尝试！");
        }else {
            saveLocation(mTbGNGGABean.getLocation().getLatitude(),mTbGNGGABean.getLocation().getLongitude());
        }
    }

    /**
     * 根据当前国际坐标获得地址信息，并保存到本地。
     * @param lat 经度
     * @param lng 纬度
     */
    private void saveLocation(final double lat, final double lng) {
        //百度地图不支持国际坐标，必须先把国际坐标转成百度坐标
        final BDLocation bdLocation = mBaiduMapModel.convertToBaiduCoord(new LatLng(lat, lng));
        //跳到目标位置
        mBaiduMapModel.focusToLocation(mMapView.getMap(),bdLocation.getLatitude(),bdLocation.getLongitude());
        //在目标位置加上一个红色标记
        mOverlay = mBaiduMapModel.addOverlayAt(mMapView, bdLocation.getLatitude(), bdLocation.getLongitude());
        //显示对话框，是否保存该位置的信息
        AlertDialogUtil.showStandardDialog(
                mActivity,
                //对话框提示的坐标为国际坐标
                composeDialogMsgSaveLocation(lat,lng),
                new Runnable() {
                    @Override
                    public void run() {
                        //调用百度api时用百度坐标
                        mBaiduMapModel.getAdjacentInfoByLatlng(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()), new GetGeoInfoListener() {
                            @Override
                            public void onGetResult(ReverseGeoCodeResult result) {
                                //保存坐标数据时，要用国际坐标
                                goToSaveLocationActivity(lat,lng,result);
                                //完成操作后要把红色标记去掉。
                                mOverlay.remove();
                            }

                            @Override
                            public void onNoResult() {
                                goToSaveLocationActivity(lat,lng,null);
                                mOverlay.remove();
                            }
                        });
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        mOverlay.remove();
                    }
                }

        );
    }

    /**
     * 跳到地址信息保存的页面去
     * @param lat 国际坐标经度
     * @param lng 国际坐标纬度
     * @param result 百度定义的一个类，封装了该地址的一些附带信息，可空。
     */
    private void goToSaveLocationActivity(double lat, double lng, @Nullable ReverseGeoCodeResult result) {
        com.skycaster.geomapper.bean.Location location=new com.skycaster.geomapper.bean.Location();
        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setBaiduCoordinateSystem(false);
        if(result!=null){
            location.setTitle(result.getAddress());
            location.setComments(result.getBusinessCircle()+result.getSematicDescription());
        }
        SaveLocationActivity.start(mActivity, location);

    }

    /**
     * 自动生成保存位置对话框中的文字内容
     * @param lat 经度
     * @param lng 纬度
     * @return 返回对话内容的字符串
     */
    private String composeDialogMsgSaveLocation(double lat, double lng) {
        StringBuffer sb=new StringBuffer();
        sb.append(mActivity.getString(R.string.latitude))
                .append(lat)
                .append('\r').append('\n')
                .append(mActivity.getString(R.string.longitude))
                .append(lng)
                .append('\r').append('\n')
                .append(mActivity.getString(R.string.confirm_if_to_add_location_record));
        return sb.toString();
    }

    /**
     * 跳出一个测量记录的清单，点击可显示测量轨迹
     */
    public void displayHistoryRoutes() {
        AlertDialogUtil.showRouteRecords(mActivity, new RouteRecordSelectedListener() {
            @Override
            public void onRouteRecordSelected(String recordName) {
                RouteRecordOpenHelper helper=new RouteRecordOpenHelper(mActivity, recordName);
                ArrayList<LatLng> routePoints = helper.getRoutePoints();
                if(mHistoryRouteOverlay!=null){
                    mHistoryRouteOverlay.remove();
                }
                mHistoryRouteOverlay = mBaiduMapModel.addHistoryRouteOverlay(mMapView.getMap(), routePoints,false);
            }

            @Override
            public void onRouteRecordEmpty() {
                mActivity.showToast("当前无测量记录。");
            }
        });
    }

    /**
     * 进入测绘模式
     */
    public void activateMappingMode() {
        mActivity.startActionMode(mActionModeCallback);
    }

    /**
     * 广播接收者，接收前台服务广播出去的北斗串口数据。
     */
    private class BeidouPortDataReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] data = intent.getByteArrayExtra(StaticData.EXTRA_BYTES_BEI_DOU_SERIAL_PORT_DATA);
            if(data!=null&&data.length>0){
                NaviDataExtractor.decipherData(data,data.length, mDataExtractorCallBack);
            }
        }
    }

}
