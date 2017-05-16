package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.skycaster.geomapper.Constants;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseMapActivity;
import com.skycaster.geomapper.util.MapUtil;

public class BaiduTraceActivity extends BaseMapActivity {

    private LocationClient mLocationClient;
    private Trace mTrace;
    private LBSTraceClient mTraceClient;
    private OnTraceListener mOnTraceListener;
    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;
    private BDLocation mLatestLocation;
    private BDLocationListener mBDLocationListener;
    private ImageView iv_toMyLocation;
    private boolean isFirstTimeGetLocation=true;


    public static void startActivity(Context context){
        context.startActivity(new Intent(context,BaiduTraceActivity.class));
    }

    @Override
    protected int setBaseLayout() {
        return R.layout.activity_baidu_trace;
    }

    @Override
    protected void initView() {
        mMapView= (TextureMapView) findViewById(R.id.activity_baidu_trace_map_view);
        mBaiduMap = mMapView.getMap();
        iv_toMyLocation= (ImageView) findViewById(R.id.activity_baidu_trace_iv_my_location);
    }

    @Override
    protected void initData() {
        //初始化百度鹰眼
        mTrace = new Trace(Constants.BAIDU_TRACE_SERVICE_ID,Constants.ENTITY_NAME,true);
        mTraceClient = new LBSTraceClient(getApplicationContext());
        mTraceClient.setInterval(5,10);
        mOnTraceListener=new OnTraceListener() {
            @Override
            public void onStartTraceCallback(int i, String s) {


            }

            @Override
            public void onStopTraceCallback(int i, String s) {

            }

            @Override
            public void onStartGatherCallback(int i, String s) {

            }

            @Override
            public void onStopGatherCallback(int i, String s) {

            }

            @Override
            public void onPushCallback(byte b, PushMessage pushMessage) {

            }
        };
        //初始化百度地图
        mLocationClient=new LocationClient(getApplicationContext());
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        mBDLocationListener=new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                showLog("location update.");
                mLatestLocation =bdLocation;
                if(isFirstTimeGetLocation){
                    MapUtil.goToMyLocation(mBaiduMap, mLatestLocation);
                    isFirstTimeGetLocation=false;
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        };
        MapUtil.initLocationClient(mLocationClient);
    }

    @Override
    protected void initListener() {

        mTraceClient.startTrace(mTrace,mOnTraceListener);

        mLocationClient.registerLocationListener(mBDLocationListener);

        iv_toMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLatestLocation !=null){
                    MapUtil.goToMyLocation(mBaiduMap, mLatestLocation);
                }else {
                    showToast("获取定位失败，请稍候尝试。");
                }
            }
        });
    }





    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        mLocationClient.stop();
        isFirstTimeGetLocation=true;
        mTraceClient.stopGather(mOnTraceListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        mLocationClient.start();
        mTraceClient.startGather(mOnTraceListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        mLocationClient.unRegisterLocationListener(mBDLocationListener);
        mTraceClient.stopTrace(mTrace,mOnTraceListener);
        mMapView.onDestroy();
    }



}
