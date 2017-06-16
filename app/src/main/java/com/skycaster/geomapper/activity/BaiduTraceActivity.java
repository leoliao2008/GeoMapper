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
import android.widget.TextView;

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
import com.skycaster.geomapper.base.BaseMapActivity;
import com.skycaster.geomapper.broadcast.PortDataReceiver;
import com.skycaster.geomapper.customized.CompassView;
import com.skycaster.geomapper.customized.LanternView;
import com.skycaster.geomapper.data.Constants;
import com.skycaster.geomapper.util.MapUtil;
import com.skycaster.inertial_navi_lib.FixQuality;
import com.skycaster.inertial_navi_lib.GPGGABean;
import com.skycaster.inertial_navi_lib.NaviDataExtractor;

import java.util.Random;


public class BaiduTraceActivity extends BaseMapActivity {

    private static final String MAP_TYPE = "MapType";
    private static final String CD_RADIO_LOC_MODE ="CDRadio_Loc_Mode";
    private static final String TRACE_MODE="OpenTraceMode";
    private static final String EAGLE_EYE_MODE="EagleEyeMode";
    private static final String MARK_TRACE_MODE="MarkTraceMode";
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
    private boolean isCdRadioLocMode;
    private MyPortDataReceiver mPortDataReceiver;
    private NaviDataExtractor.CallBack mCallBack=new NaviDataExtractor.CallBack() {
        @Override
        public void onGetGPGGABean(GPGGABean paramGPGGABean) {
            showLog("LocationData get!");
            if(isCdRadioLocMode){
                Location location = paramGPGGABean.getLocation();
                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                mLatestLocation=MapUtil.toBaiduCoord(latLng);
                mLatestLocation.setAltitude(location.getAltitude());
                updatePstRead(location.getLatitude(),location.getLongitude());
                mLanternView.updateLantern(paramGPGGABean.getFixQuality());
                if(isInTraceMode){
                    toCurrentLocation();
                }else {
                    updateCurrentLocation();
                }

            }
        }
    };
    private TextView tv_lat;
    private TextView tv_lng;
    private TextView tv_locMode;
    private boolean isInTraceMode;
    private boolean isEagleEyeMode;
    private LanternView mLanternView;
    private boolean isMarkTraceMode;




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
        tv_lat= (TextView) findViewById(R.id.activity_baidu_trace_tv_lat);
        tv_lng= (TextView) findViewById(R.id.activity_baidu_trace_tv_lng);
        tv_locMode= (TextView) findViewById(R.id.activity_baidu_trace_tv_loc_mode);
        mLanternView= (LanternView) findViewById(R.id.activity_baidu_trace_lantern_view);
    }

    @Override
    protected void initData() {

        mSharedPreferences=getSharedPreferences("Config",MODE_PRIVATE);
        isMapTypeSatellite=mSharedPreferences.getBoolean(MAP_TYPE,false);
        isCdRadioLocMode =mSharedPreferences.getBoolean(CD_RADIO_LOC_MODE,false);
        isInTraceMode=mSharedPreferences.getBoolean(TRACE_MODE,false);
        isEagleEyeMode=mSharedPreferences.getBoolean(EAGLE_EYE_MODE,false);
        isMarkTraceMode=mSharedPreferences.getBoolean(MARK_TRACE_MODE,false);

        ActionBar bar=getSupportActionBar();
        if(bar!=null){
            initActionBar(bar);
        }

        mPortDataReceiver=new MyPortDataReceiver();
        switchReceiver(isCdRadioLocMode);


        //init bd eagle eye
        mTrace = new Trace(Constants.BAIDU_TRACE_SERVICE_ID, Constants.DEVICE_NAME, true);
        mTraceClient = new LBSTraceClient(getApplicationContext());
        mTraceClient.setInterval(5,10);
        mOnTraceListener=new OnTraceListener() {

            public void onBindServiceCallback(int paramI, String paramS) {

            }

            @Override
            public void onStartTraceCallback(int paramI, String paramS) {

            }

            @Override
            public void onStopTraceCallback(int paramI, String paramS) {

            }

            @Override
            public void onStartGatherCallback(int paramI, String paramS) {

            }

            @Override
            public void onStopGatherCallback(int paramI, String paramS) {

            }

            @Override
            public void onPushCallback(byte paramB, PushMessage paramPushMessage) {

            }
        };
        //init bd map
        mLocationClient=new LocationClient(getApplicationContext());
        updateMapType(isMapTypeSatellite);
        mBaiduMap.getUiSettings().setCompassEnabled(false);
        mBaiduMap.setBuildingsEnabled(true);
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
                if(!isCdRadioLocMode){
                    mLatestLocation =bdLocation;
                    updatePstRead(mLatestLocation.getLatitude(),mLatestLocation.getLongitude());
                    if(isInTraceMode){
                        toCurrentLocation();
                    }else {
                        updateCurrentLocation();
                    }
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        };
        MapUtil.initLocationClient(mLocationClient);
    }

    private void updateLocModeUi(boolean isCdRadioLocMode){
        if(isCdRadioLocMode){
            tv_locMode.setText(getString(R.string.loc_mode_cd_radio));
            tv_locMode.setTextColor(getResources().getColor(R.color.colorWhite));
            tv_lat.setTextColor(getResources().getColor(R.color.colorWhite));
            tv_lng.setTextColor(getResources().getColor(R.color.colorWhite));
            mLanternView.setVisibility(View.VISIBLE);
        }else {
            tv_locMode.setText(getString(R.string.loc_mode_baidu));
            tv_locMode.setTextColor(getResources().getColor(R.color.colorYellow));
            tv_lat.setTextColor(getResources().getColor(R.color.colorYellow));
            tv_lng.setTextColor(getResources().getColor(R.color.colorYellow));
            mLanternView.setVisibility(View.GONE);

        }
    }

    private void updateMapType(boolean isMapTypeSatellite) {
        if(isMapTypeSatellite){
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        }else {
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        }
    }

    private void updatePstRead(final double latitude, final double longitude){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_lat.setText(String.format("%.8f",latitude)+"°N");
                tv_lng.setText(String.format("%.8f",longitude)+"°E");
            }
        });
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

    private FixQuality getRandomFixQuality() {
        FixQuality[] values = FixQuality.values();
        Random random=new Random();
        return values[random.nextInt(values.length)];
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

    private void updateCurrentLocation() {
        MapUtil.updateMyLocation(mBaiduMap,mLatestLocation);
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
        MenuItem itemCdRadioMode = menu.findItem(R.id.menu_toggle_cd_radio_mode);
        if(!isCdRadioLocMode){
            itemCdRadioMode.setTitle(getString(R.string.toggle_cd_radio_mode));
        }else {
            itemCdRadioMode.setTitle(getString(R.string.toggle_baidu_mode));
        }
        updateLocModeUi(isCdRadioLocMode);
        MenuItem itemTraceMode = menu.findItem(R.id.menu_toggle_trace_mode);
        if(isInTraceMode){
            itemTraceMode.setIcon(R.drawable.find_my_location_yellow);
        }else {
            itemTraceMode.setIcon(R.drawable.find_my_location_grey);
        }
        MenuItem eagleEyeItem = menu.findItem(R.id.menu_toggle_eagle_mode);
        if(isEagleEyeMode){
            eagleEyeItem.setIcon(R.drawable.ic_eagle_on);
        }else {
            eagleEyeItem.setIcon(R.drawable.ic_eagle_off);
        }
        MenuItem markTraceItem = menu.findItem(R.id.menu_toggle_mark_trace_mode);
        if(isMarkTraceMode){
            markTraceItem.setIcon(R.drawable.ic_trace_mode_on);
        }else {
            markTraceItem.setIcon(R.drawable.ic_trace_mode_off);
        }
        return true;
    }

    private void registerReceiver(){
        registerReceiver(mPortDataReceiver,new IntentFilter(PortDataReceiver.ACTION));
    }

    private void unRegisterReceiver(){
        try{
            unregisterReceiver(mPortDataReceiver);
        }catch (IllegalArgumentException e){

        }
    }

    private void switchReceiver(boolean isCdRadioLocMode){
        if(isCdRadioLocMode){
            registerReceiver();
        }else {
            unRegisterReceiver();
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
                updateMapType(isMapTypeSatellite);
                supportInvalidateOptionsMenu();
                break;
            case R.id.menu_toggle_cd_radio_mode:
                isCdRadioLocMode =!isCdRadioLocMode;
                mSharedPreferences.edit().putBoolean(CD_RADIO_LOC_MODE, isCdRadioLocMode).apply();
                switchReceiver(isCdRadioLocMode);
                supportInvalidateOptionsMenu();
                break;
            case R.id.menu_toggle_trace_mode:
                isInTraceMode=!isInTraceMode;
                mSharedPreferences.edit().putBoolean(TRACE_MODE,isInTraceMode).apply();
                supportInvalidateOptionsMenu();
                break;
            case R.id.menu_toggle_eagle_mode:
                isEagleEyeMode=!isEagleEyeMode;
                mSharedPreferences.edit().putBoolean(EAGLE_EYE_MODE,isEagleEyeMode).apply();
                toggleEagleEyeMode(isEagleEyeMode);
                supportInvalidateOptionsMenu();
                break;
            case R.id.menu_toggle_mark_trace_mode:
                isMarkTraceMode=!isMarkTraceMode;
                mSharedPreferences.edit().putBoolean(MARK_TRACE_MODE,isMarkTraceMode).apply();
                supportInvalidateOptionsMenu();
                break;
        }
        return true;
    }

    private void toggleEagleEyeMode(boolean isEagleEyeMode) {
        if(isEagleEyeMode){
            mTraceClient.startTrace(mTrace,mOnTraceListener);
            mTraceClient.startGather(mOnTraceListener);
            showToast(getString(R.string.eagle_system_open));
        }else {
            mTraceClient.stopGather(mOnTraceListener);
            mTraceClient.stopTrace(mTrace,mOnTraceListener);
            showToast(getString(R.string.eagle_system_close));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.registerLocationListener(mBDLocationListener);
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
        toggleEagleEyeMode(isEagleEyeMode);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.unRegisterLocationListener(mBDLocationListener);
        if(mLocationClient.isStarted()){
            mLocationClient.stop();
        }
        isFirstTimeGetLocation=true;
        toggleEagleEyeMode(false);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
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
