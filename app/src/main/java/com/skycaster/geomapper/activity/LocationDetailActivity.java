package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.ImageListAdapter;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.bean.Location;
import com.skycaster.geomapper.customized.FullLengthListView;
import com.skycaster.geomapper.util.MapUtil;

import java.util.ArrayList;

public class LocationDetailActivity extends BaseActionBarActivity {
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

    }



    @Override
    protected int getActionBarTitle() {
        return R.string.location_detail;
    }

    @Override
    protected void initRegularData() {
        mMapView.requestDisallowInterceptTouchEvent(true);

        mAdapter=new ImageListAdapter(mPicPaths,this);
        mListView.setAdapter(mAdapter);

        mActionBar = getSupportActionBar();

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
            mActionBar.setTitle(mLocation.getTitle());
            supportInvalidateOptionsMenu();
            addToPicList(mLocation.getPicList());
        }

    }

    private void addToPicList(ArrayList<String> paths) {
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
