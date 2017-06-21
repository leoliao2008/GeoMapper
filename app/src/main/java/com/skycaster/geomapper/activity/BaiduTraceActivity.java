package com.skycaster.geomapper.activity;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.PolylineOptions;
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
import com.skycaster.geomapper.data.RouteRecordOpenHelper;
import com.skycaster.geomapper.interfaces.RouteRecordSelectedListener;
import com.skycaster.geomapper.util.AlertDialogUtil;
import com.skycaster.geomapper.util.MapUtil;
import com.skycaster.geomapper.util.ToastUtil;
import com.skycaster.inertial_navi_lib.GPGGABean;
import com.skycaster.inertial_navi_lib.NaviDataExtractor;

import java.util.ArrayList;


public class BaiduTraceActivity extends BaseMapActivity {

    private static final String MAP_TYPE = "MapType";
    private static final String CD_RADIO_LOC_MODE ="CDRadio_Loc_Mode";
    private static final String TRACKING_MODE ="OpenTrackingMode";
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
                if(isInTrackingMode||isFirstTimeGetLocation){
                    toCurrentLocation();
                }else {
                    updateCurrentLocation();
                }
                if(isMarkTraceMode){
                    updateTraceLine(new LatLng(mLatestLocation.getLatitude(),mLatestLocation.getLongitude()));
                }

            }
        }
    };
    private TextView tv_lat;
    private TextView tv_lng;
    private TextView tv_locMode;
    private boolean isInTrackingMode;
    private boolean isEagleEyeMode;
    private LanternView mLanternView;
    private boolean isMarkTraceMode;
    private ArrayList<LatLng> traces=new ArrayList<>();
    private Overlay mOverlay;
    private FloatingActionButton mFAB_clearTrace;
    private boolean isClearTraceButtonVisible;
    private FloatingActionButton mFAB_saveTrace;
    private Overlay mHistoryOverlay;


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
        mFAB_clearTrace = (FloatingActionButton) findViewById(R.id.activity_baidu_trace_floating_button_clear_trace_mark);
        mFAB_saveTrace= (FloatingActionButton) findViewById(R.id.activity_baidu_trace_floating_button_save_trace_mark);
    }

    @Override
    protected void initData() {

        mSharedPreferences=getSharedPreferences("Config",MODE_PRIVATE);
        isMapTypeSatellite=mSharedPreferences.getBoolean(MAP_TYPE,false);
        isCdRadioLocMode =mSharedPreferences.getBoolean(CD_RADIO_LOC_MODE,false);
        isInTrackingMode =mSharedPreferences.getBoolean(TRACKING_MODE, false);
        isEagleEyeMode=mSharedPreferences.getBoolean(EAGLE_EYE_MODE,false);
        isMarkTraceMode=mSharedPreferences.getBoolean(MARK_TRACE_MODE,false);

        ActionBar bar=getSupportActionBar();
        if(bar!=null){
            initActionBar(bar);
        }

        mPortDataReceiver=new MyPortDataReceiver();
        switchReceiver(isCdRadioLocMode);

        mFAB_clearTrace.setImageResource(android.R.drawable.ic_menu_delete);
        mFAB_saveTrace.setImageResource(android.R.drawable.ic_menu_save);


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
                if(!isCdRadioLocMode){
                    mLatestLocation =bdLocation;
                    updatePstRead(mLatestLocation.getLatitude(),mLatestLocation.getLongitude());
                    if(isInTrackingMode||isFirstTimeGetLocation){
                        toCurrentLocation();
                    }else {
                        updateCurrentLocation();
                    }
                    if(isMarkTraceMode){
                        updateTraceLine(new LatLng(mLatestLocation.getLatitude(),mLatestLocation.getLongitude()));
                    }
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        };
        MapUtil.initLocationClient(mLocationClient);


        mBaiduMap.setMyLocationEnabled(true);
        toggleEagleEyeMode(isEagleEyeMode);

        if(isMarkTraceMode){
            toggleFloatingActionButtons(true);
        }
    }

    private void updateLocModeUi(boolean isCdRadioLocMode){
        if(isCdRadioLocMode){
            tv_locMode.setText(getString(R.string.loc_mode_cd_radio));
            tv_locMode.setTextColor(getResources().getColor(R.color.colorWhite));
            tv_lat.setTextColor(getResources().getColor(R.color.colorWhite));
            tv_lng.setTextColor(getResources().getColor(R.color.colorWhite));
            toggleLanternView(true);
        }else {
            tv_locMode.setText(getString(R.string.loc_mode_baidu));
            tv_locMode.setTextColor(getResources().getColor(R.color.colorYellow));
            tv_lat.setTextColor(getResources().getColor(R.color.colorYellow));
            tv_lng.setTextColor(getResources().getColor(R.color.colorYellow));
            toggleLanternView(false);

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

        //启动百度定位
        mLocationClient.registerLocationListener(mBDLocationListener);
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }

        //获取地图最新的旋转角度
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

        //跳到当前位置
        attachOnclick(R.id.activity_baidu_trace_iv_my_location, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mZoomLevel=21;
                if(!toCurrentLocation()){
                    showToast("获取定位失败，请稍候尝试。");
                }
            }
        });
        //获取自定义控件最新的旋转角度（？有点多余）
        mCompassView.registerOrientationChangeListener(new CompassView.OrientationChangeListener() {
            @Override
            public void onOrientationUpdate(double newDegree) {
                mRotateDegree=newDegree;
            }
        });

        //清除旧轨迹
        mFAB_clearTrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogUtil.showHint(BaiduTraceActivity.this, getString(R.string.warning_clear_trace), new Runnable() {
                    @Override
                    public void run() {
                        removeCurrentRouteOverlay();
                        if (!isMarkTraceMode) {
                            toggleFloatingActionButtons(false);
                        }
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        //do nothing.
                    }
                });
            }
        });
        //保存轨迹
        mFAB_saveTrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(traces.size()>1){
                    AlertDialogUtil.saveRoute(BaiduTraceActivity.this,traces);
                }else {
                    showToast(getString(R.string.not_enough_loc_points));
                }
            }
        });

    }

    private void addHistoryRouteOverlay(ArrayList<LatLng> routePoints) {
        if(mHistoryOverlay!=null){
            mHistoryOverlay.remove();
        }
        PolylineOptions polylineOptions = new PolylineOptions()
                .points(routePoints)
                .color(Color.DKGRAY)
                .dottedLine(true)
                .width(getResources().getInteger(R.integer.trace_line_width));
        mHistoryOverlay = mBaiduMap.addOverlay(polylineOptions);
        //// TODO: 2017/6/21 添加删除该轨迹的图标

    }


    private void toggleFloatingActionButtons(final boolean isToShow){
        final RelativeLayout.LayoutParams clearParam= (RelativeLayout.LayoutParams) mFAB_clearTrace.getLayoutParams();
        final RelativeLayout.LayoutParams saveParam= (RelativeLayout.LayoutParams) mFAB_saveTrace.getLayoutParams();
        int marginStart = saveParam.rightMargin;
        int marginStop;
        if(isToShow){
            marginStop= (int) getResources().getDimension(R.dimen.layout_margin_right_show);
        }else {
            marginStop= (int) getResources().getDimension(R.dimen.layout_margin_right_hide);
        }
        ValueAnimator animator=ValueAnimator.ofInt(marginStart,marginStop);
        animator.setDuration(500).setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                if(input==1){
                    return 1;
                }
                return (float) (input*1.1);
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int rightMargin=(int) animation.getAnimatedValue();
                if(isToShow||traces.size()<2){
                    clearParam.rightMargin=rightMargin;
                    mFAB_clearTrace.setLayoutParams(clearParam);
                    mFAB_clearTrace.invalidate();
                    saveParam.rightMargin=rightMargin;
                    mFAB_saveTrace.setLayoutParams(saveParam);
                    mFAB_saveTrace.invalidate();
                }
            }
        });
        animator.start();
    }


    private boolean toCurrentLocation() {
        boolean isSuccess;
        if(mLatestLocation !=null){
            MapUtil.goToMyLocation(mBaiduMap, mLatestLocation, mRotateDegree, mZoomLevel);
            isSuccess=true;
            if(isFirstTimeGetLocation){
                isFirstTimeGetLocation=false;
            }
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
        MenuItem itemTrackingMode = menu.findItem(R.id.menu_toggle_tracking_mode);
        if(isInTrackingMode){
            itemTrackingMode.setIcon(R.drawable.find_my_location_yellow);
        }else {
            itemTrackingMode.setIcon(R.drawable.find_my_location_grey);
        }
        MenuItem itemEagleEye = menu.findItem(R.id.menu_toggle_eagle_mode);
        if(isEagleEyeMode){
            itemEagleEye.setIcon(R.drawable.ic_eagle_on);
        }else {
            itemEagleEye.setIcon(R.drawable.ic_eagle_off);
        }
        MenuItem itemMarkTrace = menu.findItem(R.id.menu_toggle_mark_trace_mode);
        if(isMarkTraceMode){
            itemMarkTrace.setIcon(R.drawable.ic_trace_mode_on);
        }else {
            itemMarkTrace.setIcon(R.drawable.ic_trace_mode_off);
        }
        return true;
    }

    private void registerReceiver(){
        registerReceiver(mPortDataReceiver,new IntentFilter(PortDataReceiver.ACTION));
    }

    private void unRegisterReceiver(){
        try{
            unregisterReceiver(mPortDataReceiver);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
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
            case R.id.menu_toggle_tracking_mode:
                isInTrackingMode =!isInTrackingMode;
                mSharedPreferences.edit().putBoolean(TRACKING_MODE, isInTrackingMode).apply();
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
                if(isMarkTraceMode){
                    if(mLatestLocation!=null&&traces.size()<1){
                        traces.add(new LatLng(mLatestLocation.getLatitude(),mLatestLocation.getLongitude()));
                    }
                    toggleFloatingActionButtons(true);
                    ToastUtil.showToast(getString(R.string.start_marking_trace_mode));
                }else {
                    if(traces.size()<2){
                        removeCurrentRouteOverlay();
                    }
                    toggleFloatingActionButtons(false);

                    ToastUtil.showToast(getString(R.string.stop_marking_trace_mode));
                }
                supportInvalidateOptionsMenu();
                break;
            case R.id.menu_show_route_record:
                AlertDialogUtil.showRouteRecords(this, new RouteRecordSelectedListener() {
                    @Override
                    public void onRouteRecordSelected(String recordName) {
                        RouteRecordOpenHelper helper=new RouteRecordOpenHelper(BaiduTraceActivity.this, recordName);
                        ArrayList<LatLng> routePoints = helper.getRoutePoints();
                        addHistoryRouteOverlay(routePoints);
                    }
                });
                break;
        }
        return true;
    }

    private void removeCurrentRouteOverlay(){
        if(mOverlay!=null){
            mOverlay.remove();
        }
        traces.clear();
    }

    private void updateTraceLine(LatLng latLng) {
        if(mOverlay!=null){
            mOverlay.remove();
        }
        int size = traces.size();
        if(size >0){
            LatLng lastPos = traces.get(size - 1);
            if(lastPos.latitude!=latLng.latitude||lastPos.longitude!=latLng.longitude){
                traces.add(latLng);
            }
        }else {
            traces.add(latLng);
        }
        if(size >1){
            PolylineOptions polylineOptions = new PolylineOptions()
                    .points(traces)
                    .color(Color.RED)
                    .dottedLine(false)
                    .width(getResources().getInteger(R.integer.trace_line_width));
            mOverlay = mBaiduMap.addOverlay(polylineOptions);
        }

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

    private void toggleLanternView(boolean isToShow){
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLanternView.getLayoutParams();
        final int marginStart = layoutParams.leftMargin;
        int marginStop;
        if(isToShow){
            marginStop=getResources().getDimensionPixelOffset(R.dimen.margin_left_type2);
        }else {
            marginStop=getResources().getDimensionPixelOffset(R.dimen.margin_left_type3);
        }
        ValueAnimator animator=ValueAnimator.ofInt(marginStart,marginStop);
        animator.setDuration(500).setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                if(input==1){
                    return 1;
                }
                return (float) (input*1.1);
            }
        });
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.leftMargin= (int) animation.getAnimatedValue();
                mLanternView.setLayoutParams(layoutParams);
                mLanternView.invalidate();
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.unRegisterLocationListener(mBDLocationListener);
        if(mLocationClient.isStarted()){
            mLocationClient.stop();
        }
        toggleEagleEyeMode(false);
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
