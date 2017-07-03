package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.ImageListAdapter;
import com.skycaster.geomapper.base.BaseActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.bean.Location;
import com.skycaster.geomapper.customized.FullLengthListView;
import com.skycaster.geomapper.util.MapUtil;

import java.util.ArrayList;

public class LocationDetailActivity extends BaseActivity {
    public static final int CONTENT_CHANGED=368;
    public static final String LOCATION_INFO="location_info";
    private Location mLocation;
    private TextView tv_latitude;
    private TextView tv_longitude;
    private TextView tv_altitude;
    private TextView tv_comments;
    private FullLengthListView mListView;
    private ImageListAdapter mAdapter;
    private ArrayList<String> mPicPaths =new ArrayList<>();
    private NestedScrollView mNestedScrollView;
    private ProgressBar mProgressBar;
    private TextureMapView mMapView;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;


    public static void start(Context context, Location location) {
        Intent starter = new Intent(context, LocationDetailActivity.class);
        starter.putExtra(LOCATION_INFO,location);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_location_detail;
    }

    @Override
    protected void initChildViews() {
        tv_altitude= (TextView) findViewById(R.id.activity_location_detail_tv_altitude);
        tv_latitude= (TextView) findViewById(R.id.activity_location_detail_tv_latitude);
        tv_longitude= (TextView) findViewById(R.id.activity_location_detail_tv_longitude);
        tv_comments= (TextView) findViewById(R.id.activity_location_detail_tv_comments);
        mListView= (FullLengthListView) findViewById(R.id.activity_location_detail_list_view);
        mNestedScrollView= (NestedScrollView) findViewById(R.id.activity_location_detail_scroll_view);
        mProgressBar= (ProgressBar) findViewById(R.id.activity_location_detail_progress_bar);
        mMapView= (TextureMapView) findViewById(R.id.activity_location_detail_map_view);
        mToolbar= (Toolbar) findViewById(R.id.activity_location_detail_tool_bar);
        mCollapsingToolbarLayout= (CollapsingToolbarLayout) findViewById(R.id.activity_location_detail_collapsing_toolbar_layout);
        mAppBarLayout= (AppBarLayout) findViewById(R.id.activity_location_detail_app_bar_layout);

    }

    @Override
    protected void initData() {
        mMapView.requestDisallowInterceptTouchEvent(true);
        mMapView.setEnabled(false);
        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });


        mAdapter=new ImageListAdapter(mPicPaths,this);
        mListView.setAdapter(mAdapter);

        mToolbar.setTitle(getString(R.string.location_detail));
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);
        mCollapsingToolbarLayout.setTitleEnabled(true);
        mCollapsingToolbarLayout.setTitle(getString(R.string.location_detail));
        mActionBar.setTitle(getString(R.string.location_detail));

        Intent intent = getIntent();
        if(intent!=null){
            updateUi(intent);
        }
    }



    private void updateUi(Intent intent){
        Location location = (Location) intent.getSerializableExtra(LOCATION_INFO);
        if(location!=null){
            mLocation=location;
            BDLocation bdLocation;
            if(mLocation.isBaiduCoordinateSystem()){
                bdLocation=new BDLocation();
                bdLocation.setLatitude(mLocation.getLatitude());
                bdLocation.setLongitude(mLocation.getLongitude());
                bdLocation.setAltitude(mLocation.getAltitude());
            }else {
                LatLng latLng=new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
                bdLocation = MapUtil.toBaiduCoord(latLng);
                bdLocation.setAltitude(mLocation.getAltitude());
            }
            MapUtil.goToMyLocation(mMapView.getMap(),bdLocation,0,20);


            tv_latitude.setText(mLocation.getLatitude()+"°");
            tv_longitude.setText(mLocation.getLongitude()+"°");
            tv_altitude.setText(mLocation.getAltitude()+"");
            tv_comments.setText(mLocation.getComments());
            mCollapsingToolbarLayout.setTitle(mLocation.getTitle());
            updateListView(mLocation.getPicList());
        }

    }

    private void updateListView(ArrayList<String> paths) {
        mProgressBar.setVisibility(View.VISIBLE);
        if(paths.size()>0){
            mPicPaths.addAll(paths);
            mAdapter.notifyDataSetChanged();
        }
        BaseApplication.postDelay(new Runnable() {
            @Override
            public void run() {
                mNestedScrollView.fullScroll(View.FOCUS_UP);
            }
        },500);
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    protected void initListeners() {
        mCollapsingToolbarLayout.requestDisallowInterceptTouchEvent(true);
        mCollapsingToolbarLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mAppBarLayout.requestDisallowInterceptTouchEvent(true);
        mAppBarLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
