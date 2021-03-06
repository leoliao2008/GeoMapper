package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.FullSizeImageListAdapter;
import com.skycaster.geomapper.base.BaseActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.bean.Location;
import com.skycaster.geomapper.bean.Tag;
import com.skycaster.geomapper.customized.FullLengthListView;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.models.BaiduMapModel;
import com.skycaster.geomapper.util.MapUtil;

import java.util.ArrayList;

public class LocationDetailActivity extends BaseActivity {
    private Location mLocation;
    private TextView tv_latitude;
    private TextView tv_longitude;
    private TextView tv_altitude;
    private TextView tv_comments;
    private FullLengthListView mListView;
    private FullSizeImageListAdapter mAdapter;
    private ArrayList<String> mPicPaths =new ArrayList<>();
    private NestedScrollView mNestedScrollView;
    private ProgressBar mProgressBar;
    private TextureMapView mMapView;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private BaiduMap mBaiduMap;
    private MyLocationConfiguration mLocationConfig;
    private View decoratorTop;
    private View decoratorBottom;
//    private ImageView iv_edit;
    private TextView tv_tagName;
    private RelativeLayout rl_noPic;
    private TextView tv_title;
    private boolean isExpanded=true;
    private boolean hasExpandedIconBeenSet = true;
    private BaiduMapModel mBaiduMapModel;


    public static void start(Context context, Location location) {
        Intent starter = new Intent(context, LocationDetailActivity.class);
        starter.putExtra(StaticData.LOCATION_INFO,location);
        context.startActivity(starter);
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
        decoratorTop=findViewById(R.id.activity_location_detail_view_top_decoration);
        decoratorBottom=findViewById(R.id.activity_location_detail_view_bottom_decoration);
//        iv_edit= (ImageView) findViewById(R.id.activity_location_detail_iv_edit);
        tv_tagName= (TextView) findViewById(R.id.activity_location_detail_tv_tag_name);
        rl_noPic= (RelativeLayout) findViewById(R.id.activity_location_detail_rl_no_pic);
        tv_title= (TextView) findViewById(R.id.activity_location_detail_tv_loc_title);

    }

    @Override
    protected void initBaseData() {
        mBaiduMapModel=new BaiduMapModel();
        mMapView.setLogoPosition(LogoPosition.logoPostionRightBottom);
        mBaiduMap = mMapView.getMap();
        mBaiduMapModel.initBaiduMap(mMapView);
//        mBaiduMap.setMyLocationEnabled(true);


        mAdapter=new FullSizeImageListAdapter(mPicPaths,this);
        mListView.setAdapter(mAdapter);

        mCollapsingToolbarLayout.setTitleEnabled(true);
        int margin=getResources().getDimensionPixelSize(R.dimen.margin_tool_bar_expanded_title_margin);
        mCollapsingToolbarLayout.setExpandedTitleMargin(margin,margin,margin,margin);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.BLACK);
        mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.TextAppearanceExpandedTitle);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if(mActionBar!=null){
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);
        }

        Intent intent = getIntent();
        if(intent!=null){
            updateUi(intent);
        }
    }



    private void updateUi(Intent intent){
        Location location = (Location) intent.getSerializableExtra(StaticData.LOCATION_INFO);
        if(location!=null){
            mLocation=location;
            final BDLocation bdLocation;
            if(mLocation.isBaiduCoordinateSystem()){
                bdLocation=new BDLocation();
                bdLocation.setLatitude(mLocation.getLatitude());
                bdLocation.setLongitude(mLocation.getLongitude());
                bdLocation.setAltitude(mLocation.getAltitude());
            }else {
                showLog(" this is a standard gps coord");
                LatLng latLng=new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
                bdLocation = MapUtil.convertToBaiduCoord(latLng);
                bdLocation.setAltitude(mLocation.getAltitude());
            }
            int iconRsc=-1;
            switch (location.getIconStyle()){
                case 0:
                    iconRsc=R.drawable.ic_pin_1;
                    break;
                case 1:
                    iconRsc=R.drawable.ic_pin_2;
                    break;
                case 2:
                    iconRsc=R.drawable.ic_pin_3;
                    break;
                case 3:
                    iconRsc=R.drawable.ic_pin_4;
                    break;
                case 4:
                    iconRsc=R.drawable.ic_pin_5;
                    break;
                case 5:
                    iconRsc=R.drawable.ic_pin_6;
                    break;
            }
            if(iconRsc!=-1){
                mLocationConfig=new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL,
                        true,
                        BitmapDescriptorFactory.fromResource(iconRsc)
                );
            }
            showLog("move to location "+bdLocation.getLatitude()+" "+bdLocation.getLongitude());

            mBaiduMapModel.updateMyLocation(mBaiduMap,bdLocation,mLocationConfig);
            //必须间隔一小段时间执行跳跃，否则不能跳到新位置。
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBaiduMapModel.focusToLocation(mBaiduMap,bdLocation,0,20);
                }
            },500);


            tv_latitude.setText(mLocation.getLatitude()+"°");
            tv_longitude.setText(mLocation.getLongitude()+"°");
            tv_altitude.setText(mLocation.getAltitude()+"");
            tv_comments.setText(mLocation.getComments());
            Tag tag = mLocation.getTag();
            if(tag!=null){
                tv_tagName.setText(tag.getTagName());
            }
            String title = mLocation.getTitle();
            tv_title.setText(title);
            int length = title.length();
            if(length>10){
                mCollapsingToolbarLayout.setTitle(title.substring(0,10)+"......");
            }else {
                mCollapsingToolbarLayout.setTitle(title);
            }

            updateListView(mLocation.getPicList());

        }

    }

    private void updateListView(ArrayList<String> paths) {
        mProgressBar.setVisibility(View.VISIBLE);
        mPicPaths.clear();
        mPicPaths.addAll(paths);
        mAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
        if(mPicPaths.size()>0){
            rl_noPic.setVisibility(View.GONE);
        }else {
            rl_noPic.setVisibility(View.VISIBLE);
        }
        BaseApplication.postDelay(new Runnable() {
            @Override
            public void run() {
                mNestedScrollView.fullScroll(View.FOCUS_UP);
            }
        },100);
    }


    @Override
    protected void initListeners() {
        //解决百度地图和父view的滑动冲突
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
                   case MotionEvent.ACTION_UP:
                       mMapView.requestDisallowInterceptTouchEvent(false);
                       break;
                   case MotionEvent.ACTION_CANCEL:
                       mMapView.requestDisallowInterceptTouchEvent(false);
                       break;
               }
               return false;
           }
       });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(Math.abs(verticalOffset)==mCollapsingToolbarLayout.getMeasuredHeight()-mToolbar.getMeasuredHeight()){
                    decoratorBottom.setVisibility(View.GONE);
                    decoratorTop.setVisibility(View.GONE);
                    mActionBar.setHomeAsUpIndicator(null);
                    isExpanded=false;
                }else {
                    decoratorTop.setVisibility(View.VISIBLE);
                    decoratorBottom.setVisibility(View.VISIBLE);
                    mActionBar.setHomeAsUpIndicator(R.drawable.selector_back_grey_to_white);
                    isExpanded=true;
                }
                if(hasExpandedIconBeenSet !=isExpanded){
                    supportInvalidateOptionsMenu();
                }
            }
        });

//        iv_edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LocationDetailActivity.this, EditLocationActivity.class);
//                intent.putExtra(StaticData.LOCATION_INFO,mLocation);
//                startActivityForResult(intent,3241);
//            }
//        });


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
        mMapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_loc_details,menu);
        MenuItem item = menu.findItem(R.id.menu_loc_detail_edit);
        if(isExpanded){
            item.setIcon(R.drawable.selector_ic_edit_location_grey_to_white);
            hasExpandedIconBeenSet =true;
        }else {
            item.setIcon(R.drawable.selector_ic_edit_location_white_to_grey);
            hasExpandedIconBeenSet =false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_loc_detail_edit:
                Intent intent = new Intent(LocationDetailActivity.this, EditLocationActivity.class);
                intent.putExtra(StaticData.LOCATION_INFO,mLocation);
                startActivityForResult(intent,3241);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== StaticData.RESULT_CODE_MODIFICATION_SUCCESS){
            setResult(resultCode);
            updateUi(data);
        }
    }
}
