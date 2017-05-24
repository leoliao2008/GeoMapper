package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.skycaster.geomapper.data.Constants;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.base.BaseMapActivity;
import com.skycaster.geomapper.util.MapUtil;

public class BaiduTraceActivity extends BaseMapActivity {

    private static final String MAP_TYPE = "MapType";
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
    private ActionBar mActionBar;
    private boolean isMapTypeSatellite;
    private SharedPreferences mSharedPreferences;


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
        mSharedPreferences=getSharedPreferences("Config",MODE_PRIVATE);
        ActionBar bar=getSupportActionBar();
        if(bar!=null){
            initActionBar(bar);
        }
        isMapTypeSatellite=mSharedPreferences.getBoolean(MAP_TYPE,false);





        //初始化百度鹰眼
        mTrace = new Trace(Constants.BAIDU_TRACE_SERVICE_ID, Constants.DEVICE_NAME, true);
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
        if(isMapTypeSatellite){
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        }else {
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        }
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setIndoorEnable(true);
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




    private void initActionBar(ActionBar bar) {
        mActionBar=bar;
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.baidu_trace,menu);
        MenuItem item = menu.findItem(R.id.menu_toggle_map_type);
        if(isMapTypeSatellite){
            item.setIcon(R.drawable.ic_map_type_default);
        }else {
            item.setIcon(R.drawable.ic_map_type_satellite);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_toggle_map_type:
                isMapTypeSatellite=!isMapTypeSatellite;
                mSharedPreferences.edit().putBoolean(MAP_TYPE,isMapTypeSatellite).apply();
                supportInvalidateOptionsMenu();
                BaseApplication.post(new Runnable() {
                    @Override
                    public void run() {
                        changeMapType(mBaiduMap,isMapTypeSatellite);
                    }
                });
                break;
        }
        return true;
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
