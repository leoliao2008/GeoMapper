package com.skycaster.geomapper.fragment;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.SaveLocationActivity;
import com.skycaster.geomapper.adapter.MappingCoordinateRecyclerAdapter;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.base.BaseFragment;
import com.skycaster.geomapper.bean.Location;
import com.skycaster.geomapper.bean.MappingData;
import com.skycaster.geomapper.bean.MyLatLng;
import com.skycaster.geomapper.customized.SmallMarkerView;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.interfaces.CoordinateItemEditCallBack;
import com.skycaster.geomapper.models.BaiduMapModel;
import com.skycaster.geomapper.util.MapUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/14.
 */

public class MappingDataMapViewFragment extends BaseFragment {

    private TextureMapView mMapView;
    private RecyclerView mRecyclerView;
    private ArrayList<LatLng> mRealCoords=new ArrayList<>();
    private ArrayList<LatLng> mDummyCoords=new ArrayList<>();
    private static final String EXTRA_COORDINATES="coordinates";
    private static final int REQUEST_SAVE_NEW_LOCATION= 6663;
    private MappingCoordinateRecyclerAdapter mAdapter;
    private BaiduMap mBaiduMap;
    private BottomSheetBehavior<RecyclerView> mBottomSheetBehavior;
    private FloatingActionButton mFab;
    private FrameLayout fl_measureResultPanel;
    private TextView tv_measureResultPerimeter;
    private TextView tv_measureResultArea;
    private TextView tv_measureResultPathLength;
    private ImageView iv_toggle;
    private int mExpandedHeight;
    private int mCollapsedHeight;
    private CoordinatorLayout.LayoutParams mLayoutParams;
    private boolean isToShow=true;
    private IntEvaluator mEvaluator;
    private RelativeLayout fl_title;
    private int mDragIndex;
    private ArrayList<Marker>mMarkers=new ArrayList<>();
    private boolean isAddByLongClick;
    private Snackbar mSnackbar;
    private double mDistance;
    private double mPerimeter;
    private double mArea;
    private BaiduMapModel mBaiduMapModel;

    public MappingDataMapViewFragment(Context context, ArrayList<LatLng> realCoords) {
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList(EXTRA_COORDINATES, realCoords);
        setArguments(bundle);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_mapping_data_map_view;
    }

    @Override
    protected void initView() {
        mMapView= (TextureMapView) findViewById(R.id.fragment_save_mapping_data_map_view);
        mRecyclerView= (RecyclerView) findViewById(R.id.fragment_save_mapping_data_recycler_view);
        mBaiduMap=mMapView.getMap();
        mFab= (FloatingActionButton) findViewById(R.id.fragment_save_mapping_data_fab);
        fl_measureResultPanel= (FrameLayout) findViewById(R.id.content_mapping_data_result_root_view);
        tv_measureResultArea= (TextView) findViewById(R.id.content_mapping_data_result_tv_acreage);
        tv_measureResultPathLength= (TextView) findViewById(R.id.content_mapping_data_result_tv_path_length);
        tv_measureResultPerimeter= (TextView) findViewById(R.id.content_mapping_data_result_tv_perimeter);
        iv_toggle= (ImageView) findViewById(R.id.content_mapping_data_result_iv_toggle);
        fl_title= (RelativeLayout) findViewById(R.id.content_mapping_data_result_fl_panel_title);

    }

    @Override
    protected void initData(Bundle arguments) {
        ArrayList<LatLng> list = arguments.getParcelableArrayList(EXTRA_COORDINATES);
        mBaiduMapModel = new BaiduMapModel();
        if(list!=null&&list.size()>0){
            mRealCoords.addAll(list);
            for (LatLng temp:mRealCoords){
                BDLocation bdLocation = mBaiduMapModel.convertToBaiduCoord(temp);
                mDummyCoords.add(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()));
            }
        }

        mEvaluator = new IntEvaluator();


        mAdapter=new MappingCoordinateRecyclerAdapter(mRealCoords, getContext(), new CoordinateItemEditCallBack() {
            @Override
            public void onInsertNewLatlng(int newPosition, LatLng newLatlng) {
                mRealCoords.add(newPosition,newLatlng);
                BDLocation bdLocation = mBaiduMapModel.convertToBaiduCoord(newLatlng);
                mDummyCoords.add(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()));
                drawLayer(mDummyCoords);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(newPosition);
            }

            @Override
            public void onDeleteLatlng(int position, LatLng latLng) {
                if(mRealCoords.size()>1){
                    mRealCoords.remove(position);
                    mDummyCoords.remove(position);
                    drawLayer(mDummyCoords);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(position);
                }else {
                    showToast(getString(R.string.warning_keep_at_least_one_coordinate));
                }
            }

            @Override
            public void onSaveAs(int position, LatLng latLng) {
                latLng=mRealCoords.get(position);
                Location location=new Location();
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                SaveLocationActivity.startForResult(MappingDataMapViewFragment.this,REQUEST_SAVE_NEW_LOCATION,location);
            }

            @Override
            public void onEdit(int position, LatLng latLng) {
                mRealCoords.remove(position);
                mRealCoords.add(position,latLng);
                BDLocation bdLocation = mBaiduMapModel.convertToBaiduCoord(latLng);
                mDummyCoords.remove(position);
                mDummyCoords.add(position,new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()));

                drawLayer(mDummyCoords);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(position);
            }

            @Override
            public void onItemSelected(int position, LatLng latLng) {
//                BDLocation bdLocation = mBaiduMapModel.convertToBaiduCoord(latLng);
//                BDLocation location=new BDLocation();
//                location.setLatitude(latLng.latitude);
//                location.setLongitude(latLng.longitude);
                MapUtil.goToLocation(mBaiduMap,mBaiduMapModel.convertToBaiduCoord(latLng),0,21);
            }

            @Override
            public void onLongClickToGetLatlng(int position) {//百度地图不支持把百度坐标转化成国际坐标，因此此功能失去意义
                isToShow=false;
                toggleMeasureResultPanel(isToShow);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                isAddByLongClick=true;
                mSnackbar = Snackbar.make(mFab,getString(R.string.please_long_click_to_add_latlng),Snackbar.LENGTH_INDEFINITE);
                mSnackbar.show();
                onLongClickToAdd(position);
            }
        });
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);


        mBottomSheetBehavior = BottomSheetBehavior.from(mRecyclerView);

        jumpToLocationAndDrawLayer(mDummyCoords);
    }

    @Override
    protected void initListeners() {

        fl_measureResultPanel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fl_measureResultPanel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mLayoutParams = (CoordinatorLayout.LayoutParams) fl_measureResultPanel.getLayoutParams();
                mExpandedHeight = fl_measureResultPanel.getMeasuredHeight();
                mCollapsedHeight = fl_title.getMeasuredHeight();
            }
        });


        iv_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isToShow=!isToShow;
                toggleMeasureResultPanel(isToShow);
            }
        });


        mMapView.getChildAt(0).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mMapView.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mMapView.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                        mMapView.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        mMapView.requestDisallowInterceptTouchEvent(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        mMapView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        mFab.setImageResource(R.drawable.selector_ic_expand_36dp);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        mFab.setImageResource(R.drawable.selector_ic_collpase_36dp);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mFab.setImageResource(R.drawable.selector_ic_expand_36dp);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int state = mBottomSheetBehavior.getState();
                switch (state){
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                }
            }
        });

//        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
//            @Override
//            public void onMapLoaded() {
//                DisplayMetrics metrics = BaseApplication.getDisplayMetrics();
//                int x=metrics.widthPixels*9/10;
//                int y=metrics.heightPixels*4/10;
//                mMapView.setZoomControlsPosition(new Point(x,y));
//                mMapView.setScaleControlPosition(new Point(x, (int) (metrics.heightPixels*5.5/10)));
//
//            }
//        });

    }

    private void onLongClickToAdd(final int position){
        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(isAddByLongClick){
                    isAddByLongClick=false;
                    mBaiduMap.setOnMapLongClickListener(null);
                    mRealCoords.add(position,latLng);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(position);
                    mSnackbar.dismiss();
                    drawLayer(mRealCoords);
                }
            }
        });

    }

    private void toggleMeasureResultPanel(final boolean isToShow) {
        int start;
        int stop;
        if(isToShow){
            start= mCollapsedHeight;
            stop=mExpandedHeight;
        }else {
            start=mExpandedHeight;
            stop= mCollapsedHeight;
        }
        ValueAnimator animator=ValueAnimator.ofInt(start, stop);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(isToShow){
                    iv_toggle.setRotation(mEvaluator.evaluate(animation.getAnimatedFraction(), 180, 360));
                }else {
                    iv_toggle.setRotation(mEvaluator.evaluate(animation.getAnimatedFraction(), 0, 180));
                }
                mLayoutParams.height= View.MeasureSpec.makeMeasureSpec((Integer) animation.getAnimatedValue(), View.MeasureSpec.EXACTLY);
                fl_measureResultPanel.setLayoutParams(mLayoutParams);
                fl_measureResultPanel.invalidate();
            }
        });
        animator.start();
    }

    private void jumpToLocationAndDrawLayer(ArrayList<LatLng> list){
        double centerLat=0;
        double centerLng=0;
        for(LatLng temp:list){
            centerLat+=temp.latitude;
            centerLng+=temp.longitude;
        }
        int size = list.size();
        if(size>0){
            centerLat=centerLat/size;
            centerLng=centerLng/size;
        }
        MapUtil.goToLocation(mBaiduMap,centerLat,centerLng,0,20);
        drawLayer(list);
    }

    private void drawLayer(ArrayList<LatLng> list){
        mBaiduMap.clear();
        int size = list.size();
        if(size==2){
            PolylineOptions options=new PolylineOptions();
            options.width(5).points(list).color(Color.RED);
            mBaiduMap.addOverlay(options);
        }else if(size>2) {
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.fillColor(getResources().getColor(R.color.colorSkyBlueLight)).points(list);
            mBaiduMap.addOverlay(polygonOptions);

            PolylineOptions polylineOptions=new PolylineOptions();
            polylineOptions.width(5).points(list).color(Color.RED);
            mBaiduMap.addOverlay(polylineOptions);

            ArrayList<LatLng> latLngs=new ArrayList<>();
            latLngs.add(list.get(0));
            latLngs.add(list.get(size-1));
            mBaiduMap.addOverlay(new PolylineOptions().width(5).points(latLngs).color(Color.BLUE).dottedLine(true));
        }

        mMarkers.clear();
        for (int i=0;i<size;i++){
            MarkerOptions mkOpt=new MarkerOptions();
            SmallMarkerView markerView=new SmallMarkerView(getContext(),String.format("%02d",i+1));
            mkOpt.position(list.get(i))
                    .draggable(false)//不能通过拖动该变改点坐标啦，原因和不能长按增加坐标一样。
                    .icon(BitmapDescriptorFactory.fromView(markerView))
                    .anchor(0.5f,0.5f)
                    .animateType(MarkerOptions.MarkerAnimateType.grow);
            Marker marker = (Marker) mBaiduMap.addOverlay(mkOpt);
            mMarkers.add(marker);
            final int position = i;
            mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {//这个功能已经失去意义
                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    LatLng latLng = marker.getPosition();
                    mRealCoords.remove(mDragIndex);
                    mRealCoords.add(mDragIndex,latLng);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(position);
                    showToast(getString(R.string.coordinate_changes));
                    drawLayer(mRealCoords);
                }

                @Override
                public void onMarkerDragStart(Marker marker) {
                    mDragIndex = mMarkers.indexOf(marker);

                }
            });


            mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    mRecyclerView.scrollToPosition(position);
                    return true;
                }
            });
        }

        BaseApplication.post(new Runnable() {
            @Override
            public void run() {
                mDistance = 0;
                int size = mRealCoords.size();
                if(size>1){
                    for(int i = 1; i<size; i++){
                        mDistance += DistanceUtil.getDistance(mRealCoords.get(i-1), mRealCoords.get(i));

                    }
                }
                tv_measureResultPathLength.setText(String.format("%.02f", mDistance));
                mPerimeter = 0;
                if(size>2){
                    mPerimeter = mDistance +DistanceUtil.getDistance(mRealCoords.get(size-1), mRealCoords.get(0));
                    tv_measureResultPerimeter.setText(String.format("%.02f", mPerimeter));
                }else {
                    tv_measureResultPerimeter.setText(String.format("%.02f",0.f));
                }
                mArea = MapUtil.getPolygonArea(mRealCoords);
                tv_measureResultArea.setText(String.format("%.02f", mArea));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_SAVE_NEW_LOCATION){
            if(resultCode== StaticData.RESULT_CODE_MODIFICATION_SUCCESS){
                showToast(getString(R.string.save_success));
            }else {
                showToast(getString(R.string.save_fails));
            }
        }
    }

    public MappingData updateMappingData(MappingData data){
        ArrayList<MyLatLng> list=new ArrayList<>();
        for (LatLng latLng: mRealCoords){
            list.add(new MyLatLng(latLng.latitude,latLng.longitude,0));
        }
        data.setLatLngs(list);
        data.setPathLength(mDistance);
        data.setPerimeter(mPerimeter);
        data.setArea(mArea);
        return data;
    }
}
