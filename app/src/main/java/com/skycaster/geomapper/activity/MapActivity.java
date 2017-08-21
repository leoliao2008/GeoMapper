package com.skycaster.geomapper.activity;

import android.animation.IntEvaluator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.model.OnTraceListener;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.MappingCoordinateListAdapter;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.receivers.PortDataReceiver;
import com.skycaster.geomapper.customized.CompassView;
import com.skycaster.geomapper.customized.LanternView;
import com.skycaster.geomapper.customized.MappingControlPanel;
import com.skycaster.geomapper.customized.SmallMarkerView;
import com.skycaster.geomapper.data.MappingDataOpenHelper;
import com.skycaster.geomapper.data.MappingMode;
import com.skycaster.geomapper.data.RouteRecordOpenHelper;
import com.skycaster.geomapper.interfaces.CoordinateListEditCallback;
import com.skycaster.geomapper.interfaces.CreateCoordinateCallBack;
import com.skycaster.geomapper.interfaces.GetGeoInfoListener;
import com.skycaster.geomapper.interfaces.RouteRecordSelectedListener;
import com.skycaster.geomapper.util.AlertDialogUtil;
import com.skycaster.geomapper.util.MapUtil;
import com.skycaster.geomapper.util.ToastUtil;
import com.skycaster.inertial_navi_lib.GPGGABean;
import com.skycaster.inertial_navi_lib.NaviDataExtractor;

import java.util.ArrayList;
import java.util.Iterator;


public class MapActivity extends BaseActionBarActivity {

    private static final String MAP_TYPE = "MapType";
    private static final String CD_RADIO_LOC_MODE ="CDRadio_Loc_Mode";
    private static final String NAVI_MODE ="OpenTrackingMode";
    private static final String EAGLE_EYE_MODE="EagleEyeMode";
    private static final String TRACE_MODE ="MarkTraceMode";
    private static final int REQUEST_CODE_SAVE_MAPPING_DATA = 9517;
    private RelativeLayout rootView;
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
    private GPGGABean mGPGGABean;
    private NaviDataExtractor.CallBack mCallBack=new NaviDataExtractor.CallBack() {
        @Override
        public void onGetGPGGABean(GPGGABean paramGPGGABean) {
            showLog("LocationData get!");
            mGPGGABean=paramGPGGABean;
            if(isCdRadioLocMode){
                Location location = paramGPGGABean.getLocation();
                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                mLatestLocation=MapUtil.convertToBaiduCoord(latLng);
                mLatestLocation.setAltitude(location.getAltitude());
                updatePstRead(location.getLatitude(),location.getLongitude());
                mLanternView.updateLantern(paramGPGGABean.getFixQuality());
                if(isInNaviMode ||isFirstTimeGetLocation){
                    toCurrentLocation();
                }else {
                    updateCurrentLocation();
                }
                if(isDisplayCurrentTrace){
                    updateTraceLine(new LatLng(mLatestLocation.getLatitude(),mLatestLocation.getLongitude()));
                }
                MappingMode mappingMode = mMappingControlPanel.getMappingMode();
                if(isInMappingMode&&mappingMode!=null&&mappingMode==MappingMode.MAPPING_MODE_NAVI&&mMappingControlPanel.isNaviMappingStart()){
                    updateMappingCoords(new LatLng(mLatestLocation.getLatitude(),mLatestLocation.getLongitude()));
                }

            }
        }
    };

    private TextView tv_lat;
    private TextView tv_lng;
    private TextView tv_locMode;
    private boolean isInNaviMode;
    private boolean isEagleEyeMode;
    private LanternView mLanternView;
    private boolean isDisplayCurrentTrace;
    private ArrayList<LatLng> traces=new ArrayList<>();
    private Overlay mOverlay;
    private FloatingActionButton mFAB_clearTrace;
    private FloatingActionButton mFAB_saveTrace;
    private Overlay mHistoryRouteOverlay;
    private boolean isInMappingMode=false;
    private boolean isMappingByUser;
    private boolean isMappingByNavi;
    private MappingControlPanel mMappingControlPanel;
    private RadioGroup mMappingModeSelector;
    private ListView lstv_mappingCoordinates;
    private ArrayList<LatLng> mMappingCoordinates =new ArrayList<>();
    private MappingCoordinateListAdapter mCoordinateListAdapter;
    private RelativeLayout.LayoutParams mControlPanelParams;
    private RelativeLayout.LayoutParams mModeSelectorParams;
    private RelativeLayout.LayoutParams mCompassViewParams;
    private RelativeLayout.LayoutParams mMappingCoordinatesParams;
    private int mControlPanelMarginShow;
    private int mControlPanelMarginHide;
    private int mModeSelectorMarginShow;
    private int mModeSelectorMarginHide;
    private int mCompassViewMarginShow;
    private int mCompassViewMarginHide;
    private int mMappingCoordinateMarginShow;
    private int mMappingCoordinateMarginHide;
    private IntEvaluator mIntEvaluator;
    private ImageView iv_toMyLocation;
    private Overlay mMappingPolygon;
    private ArrayList<Overlay> mappingMarkers=new ArrayList<>();
    private Overlay mMappingPolylineFront;
    private Overlay mMappingPolyLineEnd;
    private RelativeLayout.LayoutParams mToMyLocationParams;
    private int mToMyLocationMarginHide;
    private int mToMyLocationMarginShow;
    private LocationManager mLocationManager;
    private MappingDataOpenHelper mMappingDataOpenHelper;


    public static void start(Context context){
        context.startActivity(new Intent(context,MapActivity.class));
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_map;
    }

    @Override
    protected void initChildViews() {
        rootView= (RelativeLayout) findViewById(R.id.activity_mapping_rl_root_view);
        mMapView= (TextureMapView) findViewById(R.id.activity_baidu_trace_map_view);
        mBaiduMap = mMapView.getMap();
        mCompassView= (CompassView) findViewById(R.id.activity_baidu_trace_compass);
        tv_lat= (TextView) findViewById(R.id.activity_baidu_trace_tv_lat);
        tv_lng= (TextView) findViewById(R.id.activity_baidu_trace_tv_lng);
        tv_locMode= (TextView) findViewById(R.id.activity_baidu_trace_tv_loc_mode);
        mLanternView= (LanternView) findViewById(R.id.activity_baidu_trace_lantern_view);
        mFAB_clearTrace = (FloatingActionButton) findViewById(R.id.activity_baidu_trace_floating_button_clear_trace_mark);
        mFAB_saveTrace= (FloatingActionButton) findViewById(R.id.activity_baidu_trace_floating_button_save_trace_mark);
        mMappingControlPanel = (MappingControlPanel) findViewById(R.id.activity_map_widget_mapping_console);
        mMappingModeSelector = (RadioGroup) findViewById(R.id.activity_map_radio_group_mapping_mode_selector);
        lstv_mappingCoordinates= (ListView) findViewById(R.id.activity_mapping_lst_view_mapping_coordinates);
        iv_toMyLocation= (ImageView) findViewById(R.id.activity_baidu_trace_iv_my_location);
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.geo_mapping;
    }

    @Override
    protected void initRegularData() {
        mSharedPreferences=getSharedPreferences("Config",MODE_PRIVATE);
        isMapTypeSatellite=mSharedPreferences.getBoolean(MAP_TYPE,false);
        isCdRadioLocMode =mSharedPreferences.getBoolean(CD_RADIO_LOC_MODE,false);
        isInNaviMode =mSharedPreferences.getBoolean(NAVI_MODE, false);
        isEagleEyeMode=mSharedPreferences.getBoolean(EAGLE_EYE_MODE,false);
        isDisplayCurrentTrace =mSharedPreferences.getBoolean(TRACE_MODE,false);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mMappingDataOpenHelper =new MappingDataOpenHelper(this);

        ActionBar bar=getSupportActionBar();
        if(bar!=null){
            initActionBar(bar);
        }

//        mMapView.showZoomControls(false);
        mCoordinateListAdapter=new MappingCoordinateListAdapter(mMappingCoordinates, this, new CoordinateListEditCallback() {
            @Override
            public void onRemove(final LatLng latLng) {
                StringBuffer sb=new StringBuffer();
                sb.append(getString(R.string.warning_delete_loc_record)).append('\r').append('\n');
                sb.append(getString(R.string.latitude)).append(latLng.latitude).append("°").append('\r').append('\n');
                sb.append(getString(R.string.longitude)).append(latLng.longitude).append("°");
                AlertDialogUtil.showHint(
                        MapActivity.this,
                        sb.toString(),
                        new Runnable() {
                            @Override
                            public void run() {
                                mMappingCoordinates.remove(latLng);
                                mCoordinateListAdapter.notifyDataSetChanged();
                                updateMappingOverLays();
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

            @Override
            public void onEdit(LatLng location, final int position) {
                AlertDialogUtil.showEditCoordinateDialog(
                        MapActivity.this,
                        location,
                        new CreateCoordinateCallBack() {
                            @Override
                            public void onCoordinateCreated(LatLng location) {
                                mMappingCoordinates.remove(position);
                                mMappingCoordinates.add(position,location);
                                mCoordinateListAdapter.notifyDataSetChanged();
                                updateMappingOverLays();
                            }
                        }
                );

            }

            @Override
            public void onInsertNewLocation(final int intoPosition) {
                AlertDialogUtil.showAddCoordinateDialog(
                        MapActivity.this,
                        new CreateCoordinateCallBack() {
                            @Override
                            public void onCoordinateCreated(LatLng location) {
                                mMappingCoordinates.add(intoPosition,location);
                                mCoordinateListAdapter.notifyDataSetChanged();
                                updateMappingOverLays();
                            }
                        }
                );
            }

            @Override
            public void onSave(LatLng latLng) {
                saveLocation(latLng,0);
            }
        });
        lstv_mappingCoordinates.setAdapter(mCoordinateListAdapter);
        mMappingControlPanel.attachToMappingActivity(mMappingCoordinates,this);


        mPortDataReceiver=new MyPortDataReceiver();
        switchReceiver(isCdRadioLocMode);

        mFAB_clearTrace.setImageResource(android.R.drawable.ic_menu_delete);
        mFAB_saveTrace.setImageResource(android.R.drawable.ic_menu_save);


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
                if(!isCdRadioLocMode){
                    mLatestLocation =bdLocation;
                    updatePstRead(mLatestLocation.getLatitude(),mLatestLocation.getLongitude());
                    if(isInNaviMode ||isFirstTimeGetLocation){
                        toCurrentLocation();
                    }else {
                        updateCurrentLocation();
                    }
                    if(isDisplayCurrentTrace){
                        updateTraceLine(new LatLng(mLatestLocation.getLatitude(),mLatestLocation.getLongitude()));
                    }
                    MappingMode mappingMode = mMappingControlPanel.getMappingMode();
                    if(isInMappingMode&&mappingMode!=null&&mappingMode==MappingMode.MAPPING_MODE_NAVI&&mMappingControlPanel.isNaviMappingStart()){
                        updateMappingCoords(new LatLng(mLatestLocation.getLatitude(),mLatestLocation.getLongitude()));
                    }
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        };
        MapUtil.initLocationClient(mLocationClient);

        mIntEvaluator = new IntEvaluator();
        //测量模式下各个view的初始位置
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                initMappingUiData();
            }
        });

        mBaiduMap.setMyLocationEnabled(true);

        if(isDisplayCurrentTrace){
            toggleFloatingActionButtons(true);
        }

    }

    private void initMappingUiData() {
        mControlPanelParams = (RelativeLayout.LayoutParams) mMappingControlPanel.getLayoutParams();
        mModeSelectorParams = (RelativeLayout.LayoutParams) mMappingModeSelector.getLayoutParams();
        mCompassViewParams = (RelativeLayout.LayoutParams) mCompassView.getLayoutParams();
        mMappingCoordinatesParams = (RelativeLayout.LayoutParams) lstv_mappingCoordinates.getLayoutParams();
        mToMyLocationParams = (RelativeLayout.LayoutParams) iv_toMyLocation.getLayoutParams();

        mControlPanelMarginShow = 1;
        mControlPanelMarginHide = mControlPanelParams.rightMargin;

        mModeSelectorMarginShow = 1;
        mModeSelectorMarginHide = mModeSelectorParams.rightMargin;

        mToMyLocationMarginHide = mToMyLocationParams.bottomMargin;
        mToMyLocationMarginShow = lstv_mappingCoordinates.getMeasuredHeight();

        int marginTop = BaseApplication.getDisplayMetrics().heightPixels - mCompassView.getMeasuredHeight()-mActionBar.getHeight()-10;
        int marginLeft=(BaseApplication.getDisplayMetrics().widthPixels-mCompassView.getMeasuredWidth())/2;
        mCompassViewParams.topMargin=marginTop;
        mCompassViewParams.leftMargin=marginLeft;
        mCompassView.setLayoutParams(mCompassViewParams);
        mCompassView.requestLayout();


        mCompassViewMarginShow =BaseApplication.getDisplayMetrics().widthPixels-mCompassView.getMeasuredWidth()-mActionBar.getHeight();
        mCompassViewMarginHide = mCompassViewParams.leftMargin;

        mMappingCoordinateMarginShow = getResources().getDimensionPixelOffset(R.dimen.margin_smallest);
        mMappingCoordinateMarginHide = mMappingCoordinatesParams.bottomMargin;
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

    private void updateMappingCoords(final LatLng latLng) {
        if(!isSameAsLast(latLng)){
            BaseApplication.post(new Runnable() {
                @Override
                public void run() {
                    mMappingCoordinates.add(latLng);
                    mCoordinateListAdapter.notifyDataSetChanged();
                    lstv_mappingCoordinates.smoothScrollToPosition(Integer.MAX_VALUE);
                    updateMappingOverLays();
                }
            });
        }
    }

    private boolean isSameAsLast(LatLng latLng){
        int size = mMappingCoordinates.size();
        if(size>0){
            LatLng last = mMappingCoordinates.get(size - 1);
            return last.latitude==latLng.latitude&&last.longitude==latLng.longitude;
        }
        return false;
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
                AlertDialogUtil.showHint(MapActivity.this, getString(R.string.warning_clear_trace), new Runnable() {
                    @Override
                    public void run() {
                        removeCurrentRouteOverlay();
                        if (!isDisplayCurrentTrace) {
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
                    AlertDialogUtil.saveRoute(MapActivity.this,traces);
                }else {
                    showToast(getString(R.string.not_enough_loc_points));
                }
            }
        });

        //长点增加地址记录
        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                saveLocation(latLng,0);
            }
        });
        //在手工测量模式下每点击一次，就增加一个测量基点
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MappingMode mappingMode = mMappingControlPanel.getMappingMode();
                if(isInMappingMode){
                    if(mappingMode!=null&&mappingMode==MappingMode.MAPPING_MODE_USER){
                        updateMappingCoords(latLng);
                    }
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        //点选测量模式
        mMappingModeSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int buttonId = group.getCheckedRadioButtonId();
                switch (buttonId){
                    case R.id.activity_map_rbtn_map_by_navi:
                        mMappingControlPanel.setMappingMode(MappingMode.MAPPING_MODE_NAVI);
                        break;
                    case R.id.activity_map_rbtn_map_by_user:
                        mMappingControlPanel.setMappingMode(MappingMode.MAPPING_MODE_USER);
                        mMappingControlPanel.setNaviMappingStart(false);
                        break;
                }
            }
        });

        //测量模式下点击坐标将跳到地图相应位置
        lstv_mappingCoordinates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LatLng latLng = mMappingCoordinates.get(position);
                BDLocation location=new BDLocation();
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                MapUtil.goToLocation(mBaiduMap,location,mRotateDegree,21);
            }
        });

    }

    private void saveLocation(final LatLng latLng, final double altitude) {
        MarkerOptions overlayOptions= new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_loc));
        overlayOptions.title("test");
        final BDLocation bdLocation=new BDLocation();
        bdLocation.setLatitude(latLng.latitude);
        bdLocation.setLongitude(latLng.longitude);
        bdLocation.setAltitude(altitude);
        MapUtil.goToLocation(mBaiduMap,bdLocation,mRotateDegree,21);
        final Overlay overlay = mBaiduMap.addOverlay(overlayOptions);


        BaseApplication.postDelay(new Runnable() {
            @Override
            public void run() {
                StringBuffer sb=new StringBuffer();
                sb.append(getString(R.string.latitude))
                        .append(latLng.latitude)
                        .append('\r').append('\n')
                        .append(getString(R.string.longitude))
                        .append(latLng.longitude)
                        .append('\r').append('\n')
                        .append(getString(R.string.confirm_if_to_add_location_record));
                AlertDialogUtil.showHint(
                        MapActivity.this,
                        sb.toString(),
                        new Runnable() {
                            @Override
                            public void run() {
                                MapUtil.getAdjacentInfoByLatlng(latLng, new GetGeoInfoListener() {
                                    @Override
                                    public void onGetResult(ReverseGeoCodeResult result) {
                                        com.skycaster.geomapper.bean.Location location=new com.skycaster.geomapper.bean.Location();
                                        location.setTitle(result.getAddress());
                                        location.setLatitude(bdLocation.getLatitude());
                                        location.setLongitude(bdLocation.getLongitude());
                                        location.setAltitude(bdLocation.getAltitude());
                                        location.setBaiduCoordinateSystem(true);
                                        location.setComments(result.getBusinessCircle()+result.getSematicDescription());
                                        SaveLocationActivity.start(MapActivity.this, location);
                                        overlay.remove();
                                    }

                                    @Override
                                    public void onNoResult() {
                                        com.skycaster.geomapper.bean.Location location=new com.skycaster.geomapper.bean.Location();
                                        location.setLatitude(bdLocation.getLatitude());
                                        location.setLongitude(bdLocation.getLongitude());
                                        location.setAltitude(bdLocation.getAltitude());
                                        location.setBaiduCoordinateSystem(true);
                                        SaveLocationActivity.start(MapActivity.this, location);
                                        overlay.remove();
                                    }
                                });
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                overlay.remove();
                            }
                        }

                );
            }
        },500);
    }

    private void addHistoryRouteOverlay(ArrayList<LatLng> routePoints) {
        if(mHistoryRouteOverlay !=null){
            mHistoryRouteOverlay.remove();
        }
        PolylineOptions polylineOptions = new PolylineOptions()
                .points(routePoints)
                .color(Color.DKGRAY)
                .dottedLine(true)
                .width(getResources().getInteger(R.integer.trace_line_width));
        mHistoryRouteOverlay = mBaiduMap.addOverlay(polylineOptions);

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
            MapUtil.goToMyLocation(mBaiduMap, mLatestLocation, null,mRotateDegree, mZoomLevel);
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
        MapUtil.updateMyLocation(mBaiduMap,mLatestLocation,null);
    }

    private void initActionBar(ActionBar bar) {
        mActionBar=bar;
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_activity,menu);
        MenuItem itemMapType = menu.findItem(R.id.menu_toggle_map_type);
        if(isMapTypeSatellite){
            itemMapType.setTitle(getString(R.string.switch_to_normal_map_type));
        }else {
            itemMapType.setTitle(getString(R.string.switch_to_satellite_map_type));
        }
        MenuItem itemCdRadioMode = menu.findItem(R.id.menu_toggle_cd_radio_mode);
        if(!isCdRadioLocMode){
            itemCdRadioMode.setTitle(getString(R.string.toggle_cd_radio_mode));
        }else {
            itemCdRadioMode.setTitle(getString(R.string.toggle_baidu_mode));
        }
        updateLocModeUi(isCdRadioLocMode);
        MenuItem itemTrackingMode = menu.findItem(R.id.menu_toggle_navi_mode);
        if(isInNaviMode){
            itemTrackingMode.setIcon(R.drawable.ic_navi_mode_on);
        }else {
            itemTrackingMode.setIcon(R.drawable.ic_navi_mode_off);
        }
        MenuItem itemMarkTrace = menu.findItem(R.id.menu_toggle_display_current_trace);
        if(isDisplayCurrentTrace){
            itemMarkTrace.setIcon(R.drawable.ic_trace_mode_on);
        }else {
            itemMarkTrace.setIcon(R.drawable.ic_trace_mode_off);
        }
        MenuItem itemMappingMode = menu.findItem(R.id.menu_toggle_mapping_mode);
        if(isInMappingMode){
            itemMappingMode.setIcon(R.drawable.ic_mapping_mode_on);
        }else {
            itemMappingMode.setIcon(R.drawable.ic_mapping_mode_off);
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
            //切换地图类型
            case R.id.menu_toggle_map_type:
                isMapTypeSatellite=!isMapTypeSatellite;
                mSharedPreferences.edit().putBoolean(MAP_TYPE,isMapTypeSatellite).apply();
                updateMapType(isMapTypeSatellite);
                supportInvalidateOptionsMenu();
                break;
            //切换定位模式
            case R.id.menu_toggle_cd_radio_mode:
                isCdRadioLocMode =!isCdRadioLocMode;
                mSharedPreferences.edit().putBoolean(CD_RADIO_LOC_MODE, isCdRadioLocMode).apply();
                switchReceiver(isCdRadioLocMode);
                supportInvalidateOptionsMenu();
                break;
            //开/关跟踪模式
            case R.id.menu_toggle_navi_mode:
                isInNaviMode =!isInNaviMode;
                mSharedPreferences.edit().putBoolean(NAVI_MODE, isInNaviMode).apply();
                supportInvalidateOptionsMenu();
                break;
//            case R.id.menu_toggle_eagle_mode:
//                isEagleEyeMode=!isEagleEyeMode;
//                mSharedPreferences.edit().putBoolean(EAGLE_EYE_MODE,isEagleEyeMode).apply();
//                toggleEagleEyeMode(isEagleEyeMode);
//                supportInvalidateOptionsMenu();
//                break;
            //开始/停止记录移动轨迹
            case R.id.menu_toggle_display_current_trace:
                if(!isInMappingMode){
                    toggleDisplayCurrentTrace();
                    supportInvalidateOptionsMenu();
                }else {
                    showToast(getString(R.string.waring_conflict_with_mapping_mode));
                }
                break;
            //选择历史轨迹
            case R.id.menu_show_route_record:
                AlertDialogUtil.showRouteRecords(this, new RouteRecordSelectedListener() {
                    @Override
                    public void onRouteRecordSelected(String recordName) {
                        RouteRecordOpenHelper helper=new RouteRecordOpenHelper(MapActivity.this, recordName);
                        ArrayList<LatLng> routePoints = helper.getRoutePoints();
                        addHistoryRouteOverlay(routePoints);
                    }

                    @Override
                    public void onRouteRecordEmpty() {
                        //do nothing
                    }
                });
                break;
            //清除所有历史轨迹
            case R.id.menu_clear_route_record:
                if(mHistoryRouteOverlay !=null){
                    mHistoryRouteOverlay.remove();
                    mHistoryRouteOverlay =null;
                }
                break;
            //保存当前位置信息
            case R.id.menu_save_current_position:
                if(isCdRadioLocMode){
                    if(mGPGGABean!=null){
                        BDLocation bdLocation = MapUtil.convertToBaiduCoord(new LatLng(mGPGGABean.getLocation().getLatitude(), mGPGGABean.getLocation().getLongitude()));
                        bdLocation.setAltitude(mGPGGABean.getLocation().getAltitude());
                        saveLocation(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()),mGPGGABean.getLocation().getAltitude());
                    }else {
                        showToast(getString(R.string.current_location_is_null));
                    }

                }else {
                    if(mLatestLocation!=null){
                        saveLocation(new LatLng(mLatestLocation.getLatitude(),mLatestLocation.getLongitude()),mLatestLocation.getAltitude());
                    }else {
                        showToast(getString(R.string.current_location_is_null));
                    }
                }
                break;
            //启动/关闭测量模式
            case R.id.menu_toggle_mapping_mode:
                if(isInMappingMode&&mMappingCoordinates.size()>1){
                    AlertDialogUtil.showHint(
                            this,
                            getString(R.string.waring_clear_unsave_mapping_data),
                            new Runnable() {
                                @Override
                                public void run() {
                                    toggleMappingMode();
                                }
                            },
                            new Runnable() {
                                @Override
                                public void run() {
                                    //do nothing
                                }
                            }
                    );
                }else {
                    toggleMappingMode();
                }
                break;
        }
        return true;
    }

    private void toggleMappingMode() {
        isInMappingMode=!isInMappingMode;
        if(isInMappingMode&& isDisplayCurrentTrace){
            toggleDisplayCurrentTrace();
        }
        animateToggleMappingUIs(isInMappingMode);
        if(!isInMappingMode){
            mMappingCoordinates.clear();
            mMappingControlPanel.setNaviMappingStart(false);
            if(mMappingPolygon !=null){
                mMappingPolygon.remove();
            }
            if(mMappingPolylineFront !=null){
                mMappingPolylineFront.remove();
            }
            if(mMappingPolyLineEnd!=null){
                mMappingPolyLineEnd.remove();
            }
            for(Overlay overlay:mappingMarkers){
                overlay.remove();
            }
            mappingMarkers.clear();
            mMappingControlPanel.updateLengthAndAcreage();
        }
        supportInvalidateOptionsMenu();

    }

    private void toggleDisplayCurrentTrace() {
        isDisplayCurrentTrace =!isDisplayCurrentTrace;
        mSharedPreferences.edit().putBoolean(TRACE_MODE, isDisplayCurrentTrace).apply();
        if(isDisplayCurrentTrace){
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

    public void updateMappingOverLays(){
        if(mMappingPolygon !=null){
            mMappingPolygon.remove();
        }
        if(mMappingPolylineFront !=null){
            mMappingPolylineFront.remove();
        }
        if(mMappingPolyLineEnd!=null){
            mMappingPolyLineEnd.remove();
        }
        int size = mMappingCoordinates.size();
        //显示测量范围
        if(size >=3){
            PolygonOptions options=new PolygonOptions()
                    .points(mMappingCoordinates)
                    .fillColor(getResources().getColor(R.color.colorSkyBlueLight))
                    .stroke(new Stroke(15,15));
            mMappingPolygon = mBaiduMap.addOverlay(options);
        }
        //显示测量坐标
        for(Overlay overlay:mappingMarkers){
            overlay.remove();
        }
        mappingMarkers.clear();
        for(int i = 0, len = size; i<len; i++){
            String index=String.format("%02d",i+1);
            LatLng latLng = mMappingCoordinates.get(i);
            MarkerOptions options=new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromView(new SmallMarkerView(MapActivity.this,index)))
                    .anchor(0.5f,0.5f)
                    .position(latLng);
            Overlay overlay = mBaiduMap.addOverlay(options);
            mappingMarkers.add(overlay);
        }
        //显示行走路径
        if(size >1){
            PolylineOptions options=new PolylineOptions()
                    .points(mMappingCoordinates)
                    .color(Color.RED)
                    .width(5)
                    .dottedLine(true);
            mMappingPolylineFront = mBaiduMap.addOverlay(options);

            //闭合线
            if(size>2){
                ArrayList<LatLng> list=new ArrayList<>();
                list.add(mMappingCoordinates.get(size-1));
                list.add(mMappingCoordinates.get(0));
                PolylineOptions opt=new PolylineOptions()
                        .points(list)
                        .color(Color.BLUE)
                        .width(5)
                        .dottedLine(true);
                mMappingPolyLineEnd = mBaiduMap.addOverlay(opt);
            }
        }
        //更新面积、路径长度
        mMappingControlPanel.updateLengthAndAcreage();
    }



    private void toggleLanternView(boolean isToShow){
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLanternView.getLayoutParams();
        final int marginStart = layoutParams.leftMargin;
        int marginStop;
        if(isToShow){
            marginStop=5;
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
        checkIfGpsOpen();
    }

    private void checkIfGpsOpen() {
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialogUtil.showHint(this, getString(R.string.advise_to_open_gps), new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(intent);
                    }catch (ActivityNotFoundException e1){
                        intent.setAction(Settings.ACTION_SETTINGS);
                        try {
                            startActivity(intent);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }, new Runnable() {
                @Override
                public void run() {
                    showToast(getString(R.string.malfunction_for_gps_not_available));
                }
            });

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMappingDataOpenHelper.close();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.unRegisterLocationListener(mBDLocationListener);
        if(mLocationClient.isStarted()){
            mLocationClient.stop();
        }
//        toggleEagleEyeMode(false);
        mMapView.onDestroy();
        unRegisterReceiver();
    }

    public void saveMappingData(){

        if(mMappingCoordinates.size()>1){
            ArrayList<LatLng> clone = new ArrayList<>();
            Iterator<LatLng> iterator = mMappingCoordinates.iterator();
            while (iterator.hasNext()){
                clone.add(iterator.next());
            }
            SaveMappingDataActivity.startForResult(this,REQUEST_CODE_SAVE_MAPPING_DATA,clone);
        }else {
            showToast(getString(R.string.not_enough_loc_points));
        }
    }



    class MyPortDataReceiver extends PortDataReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] bytes = intent.getByteArrayExtra(PortDataReceiver.DATA);
            NaviDataExtractor.decipherData(bytes, bytes.length,mCallBack);
        }
    }

    private void animateToggleMappingUIs(final boolean isShow){
        ValueAnimator animator=ValueAnimator.ofInt(1,100);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction=animation.getAnimatedFraction();
                if(isShow){
                    mControlPanelParams.rightMargin= mIntEvaluator.evaluate(fraction,mControlPanelMarginHide,mControlPanelMarginShow);
                    mCompassViewParams.leftMargin= mIntEvaluator.evaluate(fraction,mCompassViewMarginHide,mCompassViewMarginShow);
                    mModeSelectorParams.rightMargin=mIntEvaluator.evaluate(fraction,mModeSelectorMarginHide,mModeSelectorMarginShow);
                    mMappingCoordinatesParams.bottomMargin=mIntEvaluator.evaluate(fraction,mMappingCoordinateMarginHide,mMappingCoordinateMarginShow);
                    mToMyLocationParams.bottomMargin=mIntEvaluator.evaluate(fraction,mToMyLocationMarginHide,mToMyLocationMarginShow);
                }else {
                    mControlPanelParams.rightMargin= mIntEvaluator.evaluate(fraction,mControlPanelMarginShow,mControlPanelMarginHide);
                    mCompassViewParams.leftMargin= mIntEvaluator.evaluate(fraction,mCompassViewMarginShow,mCompassViewMarginHide);
                    mModeSelectorParams.rightMargin=mIntEvaluator.evaluate(fraction,mModeSelectorMarginShow,mModeSelectorMarginHide);
                    mMappingCoordinatesParams.bottomMargin=mIntEvaluator.evaluate(fraction,mMappingCoordinateMarginShow,mMappingCoordinateMarginHide);
                    mToMyLocationParams.bottomMargin=mIntEvaluator.evaluate(fraction,mToMyLocationMarginShow,mToMyLocationMarginHide);
                }

                mMappingControlPanel.setLayoutParams(mControlPanelParams);
                mCompassView.setLayoutParams(mCompassViewParams);
                mMappingModeSelector.setLayoutParams(mModeSelectorParams);
                lstv_mappingCoordinates.setLayoutParams(mMappingCoordinatesParams);
                iv_toMyLocation.setLayoutParams(mToMyLocationParams);

                rootView.requestLayout();

            }
        });
        animator.start();
        if(!isShow){
            mMappingControlPanel.setNaviMappingStart(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_SAVE_MAPPING_DATA){
            if(resultCode==RESULT_OK){
                showToast(getString(R.string.save_success));
            }else if(resultCode==RESULT_CANCELED){
                showToast(getString(R.string.save_cancel));
            }
        }
    }
}
