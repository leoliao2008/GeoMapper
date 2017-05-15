package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.skycaster.geomapper.Constants;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActivity;

public class BaiduTraceActivity extends BaseActivity {


    private Trace mTrace;
    private LBSTraceClient mTraceClient;
    private OnTraceListener mOnTraceListener;
    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;


    public static void startActivity(Context context){
        context.startActivity(new Intent(context,BaiduTraceActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //百度地图启动前需初始化
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setBaseLayout() {
        return R.layout.activity_baidu_trace;
    }

    @Override
    protected void initView() {
        mMapView= (TextureMapView) findViewById(R.id.activity_baidu_trace_map_view);
        mBaiduMap = mMapView.getMap();
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
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);




    }

    @Override
    protected void initListener() {
        mTraceClient.startTrace(mTrace,mOnTraceListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTraceClient.stopGather(mOnTraceListener);
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTraceClient.startGather(mOnTraceListener);
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTraceClient.stopTrace(mTrace,mOnTraceListener);
        mMapView.onDestroy();
    }
}
