package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.base.BaseMapActivity;
import com.skycaster.geomapper.broadcast.PortDataReceiver;
import com.skycaster.geomapper.customized.CompassView;
import com.skycaster.geomapper.data.Constants;
import com.skycaster.geomapper.util.MapUtil;
import com.skycaster.inertial_navi_lib.GPGGABean;
import com.skycaster.inertial_navi_lib.NaviDataExtractor;


public class BaiduTraceActivity extends BaseMapActivity {

    private static final String MAP_TYPE = "MapType";
    private static final String SYNCHRONISE_PST ="SynchronisePosition";
    private static final String CD_RADIO_MODE="CdRadioMode";
    private LocationClient mLocationClient;
    private Trace mTrace;
    private LBSTraceClient mTraceClient;
    private OnTraceListener mOnTraceListener;
    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;
    private BDLocation mLatestLocation;
    private BDLocationListener mBDLocationListener;
    private boolean isFirstTimeGetLocation=true;
    private ActionBar mActionBar;
    private boolean isMapTypeSatellite;
    private SharedPreferences mSharedPreferences;
    private CompassView mCompassView;
    private int mZoomLevel =100;
    private double mRotateDegree =0;
    private boolean isBaiduTraceMode;
    private boolean isCdRadioMode;
    private MyPortDataReceiver mPortDataReceiver;
    private NaviDataExtractor.CallBack mCallBack=new NaviDataExtractor.CallBack() {
        @Override
        public void onGetGPGGABean(GPGGABean paramGPGGABean) {
            showLog("LocationData get!");
            if(isCdRadioMode){
                Location location = paramGPGGABean.getLocation();
                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                mLatestLocation=MapUtil.toBaiduCoord(latLng);
                mLatestLocation.setAltitude(location.getAltitude());
                toCurrentLocation();
            }
        }
    };


    public static void startActivity(Context context){
        context.startActivity(new Intent(context,BaiduTraceActivity.class));
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_baidu_trace;
    }

    @Override
    protected void initChildViews() {
        mMapView= (TextureMapView) findViewById(R.id.activity_baidu_trace_map_view);
        mBaiduMap = mMapView.getMap();
        mCompassView= (CompassView) findViewById(R.id.activity_baidu_trace_compass);
    }

    @Override
    protected void initData() {
        mSharedPreferences=getSharedPreferences("Config",MODE_PRIVATE);
        isMapTypeSatellite=mSharedPreferences.getBoolean(MAP_TYPE,false);
        isBaiduTraceMode =mSharedPreferences.getBoolean(SYNCHRONISE_PST,false);
        isCdRadioMode=mSharedPreferences.getBoolean(CD_RADIO_MODE,false);

        ActionBar bar=getSupportActionBar();
        if(bar!=null){
            initActionBar(bar);
        }

        mPortDataReceiver=new MyPortDataReceiver();


        //init bd eagle eye
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
        //init bd map
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
                if(isFirstTimeGetLocation){
                    mLatestLocation =bdLocation;
                    isFirstTimeGetLocation=false;
                    toCurrentLocation();
                }
                if(!isCdRadioMode){
                    mLatestLocation =bdLocation;
                    if(isBaiduTraceMode){
                        toCurrentLocation();
                    }
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        };
        MapUtil.initLocationClient(mLocationClient);
    }



    @Override
    protected void initListeners() {

        //map status upgrade listener
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                mZoomLevel = (int) mapStatus.zoom;
                mRotateDegree=mapStatus.rotate;

            }
        });

        //on click to my location
        attachOnclick(R.id.activity_baidu_trace_iv_my_location, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mZoomLevel=21;
                if(!toCurrentLocation()){
                    showToast("获取定位失败，请稍候尝试。");
                }
            }
        });
        //set up map orientation update
        mCompassView.registerOrientationChangeListener(new CompassView.OrientationChangeListener() {
            @Override
            public void onOrientationUpdate(double newDegree) {
                mRotateDegree=newDegree;
            }
        });


    }

    private boolean toCurrentLocation() {
        boolean isSuccess;
        if(mLatestLocation !=null){
            MapUtil.goToMyLocation(mBaiduMap, mLatestLocation, mRotateDegree, mZoomLevel);
            isSuccess=true;
        }else {
            isSuccess=false;
        }
        return isSuccess;
    }


    private void initActionBar(ActionBar bar) {
        mActionBar=bar;
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_baidu_trace,menu);
        MenuItem itemMapType = menu.findItem(R.id.menu_toggle_map_type);
        if(isMapTypeSatellite){
            itemMapType.setIcon(R.drawable.ic_map_type_satellite);
        }else {
            itemMapType.setIcon(R.drawable.ic_map_type_default);
        }
        MenuItem itemBaiduMode=menu.findItem(R.id.menu_switch_synchronise_position);
        if(isBaiduTraceMode){
            itemBaiduMode.setIcon(R.drawable.ic_baidu_mode_on);
        }else {
            itemBaiduMode.setIcon(R.drawable.ic_baidu_mode_off);
        }
        MenuItem itemCdRadioMode = menu.findItem(R.id.menu_toggle_cd_radio_mode);
        if(isCdRadioMode){
            itemCdRadioMode.setIcon(R.drawable.ic_cd_radio_mode_on);
        }else {
            itemCdRadioMode.setIcon(R.drawable.ic_cd_radio_mode_off);
        }
        return true;
    }

    private void unRegisterReceiver(){
        try{
            unregisterReceiver(mPortDataReceiver);
        }catch (IllegalArgumentException e){

        }
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
            case R.id.menu_switch_synchronise_position:
                isBaiduTraceMode =!isBaiduTraceMode;
                mSharedPreferences.edit().putBoolean(SYNCHRONISE_PST, isBaiduTraceMode).apply();
                if(isCdRadioMode){
                    isCdRadioMode=false;
                    mSharedPreferences.edit().putBoolean(CD_RADIO_MODE,isCdRadioMode).apply();
                    unRegisterReceiver();
                }
                supportInvalidateOptionsMenu();
                break;
            case R.id.menu_toggle_cd_radio_mode:
                isCdRadioMode=!isCdRadioMode;
                mSharedPreferences.edit().putBoolean(CD_RADIO_MODE,isCdRadioMode).apply();
                if(isCdRadioMode){
                    if(isBaiduTraceMode){
                        isBaiduTraceMode=false;
                        mSharedPreferences.edit().putBoolean(SYNCHRONISE_PST, isBaiduTraceMode).apply();
                    }
                    registerReceiver(mPortDataReceiver,new IntentFilter(PortDataReceiver.ACTION));
                }else {
                    unRegisterReceiver();
                }
                supportInvalidateOptionsMenu();
                break;

        }
        return true;
    }



    @Override
    protected void onStart() {
        super.onStart();
        mTraceClient.startTrace(mTrace,mOnTraceListener);
        mLocationClient.registerLocationListener(mBDLocationListener);
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
        mTraceClient.startGather(mOnTraceListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTraceClient.stopTrace(mTrace,mOnTraceListener);
        mLocationClient.unRegisterLocationListener(mBDLocationListener);
        if(mLocationClient.isStarted()){
            mLocationClient.stop();
        }
        isFirstTimeGetLocation=true;
        mTraceClient.stopGather(mOnTraceListener);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.unRegisterLocationListener(mBDLocationListener);
        mLocationClient.stop();
        mTraceClient.stopTrace(mTrace,mOnTraceListener);
        mMapView.onDestroy();
        unRegisterReceiver();
    }

    class MyPortDataReceiver extends PortDataReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] bytes = intent.getByteArrayExtra(PortDataReceiver.DATA);
            NaviDataExtractor.decipherData(bytes, bytes.length,mCallBack);
        }
    }



}
