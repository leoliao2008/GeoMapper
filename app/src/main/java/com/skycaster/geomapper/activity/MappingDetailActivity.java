package com.skycaster.geomapper.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.MappingDetailPicListAdapter;
import com.skycaster.geomapper.base.BaseActivity;
import com.skycaster.geomapper.bean.MappingData;
import com.skycaster.geomapper.bean.MyLatLng;
import com.skycaster.geomapper.customized.FullLengthListView;
import com.skycaster.geomapper.customized.MediumMarkerView;
import com.skycaster.geomapper.data.Constants;
import com.skycaster.geomapper.util.MapUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/20.
 */

public class MappingDetailActivity extends BaseActivity {

    private static final int REQUEST_CODE_EDIT_DATA = 9517;
    private TextureMapView mMapView;
    private TextView tv_title;
    private TextView tv_locCount;
    private TextView tv_perimeter;
    private TextView tv_pathLength;
    private TextView tv_area;
    private TextView tv_address;
    private TextView tv_comments;
    private TextView tv_tagName;
    private FullLengthListView mListView;
    private RelativeLayout rl_noPic;
    private MappingData mMappingData;
    private MappingDetailPicListAdapter mAdapter;
    private BaiduMap mBaiduMap;
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;
    private int mCollpasePoint;
    private boolean isExpanded;
    private boolean shouldExpand;
    private Intent mIntent;


    public static void start(Context context, MappingData data) {
        Intent intent = new Intent(context, MappingDetailActivity.class);
        intent.putExtra(Constants.MAPPING_DATA_SOURCE,data);
        context.startActivity(intent);
    }

    public static void startForResult(Activity activity,MappingData data,int requestCode){
        Intent intent = new Intent(activity, MappingDetailActivity.class);
        intent.putExtra(Constants.MAPPING_DATA_SOURCE,data);
        activity.startActivityForResult(intent,requestCode);

    }

    public static void startForResult(Fragment fragment,MappingData data,int groupPosition,int childPosition,int requestCode){
        Intent intent=new Intent(fragment.getContext(),MappingDetailActivity.class);
        intent.putExtra(Constants.MAPPING_DATA_SOURCE,data);
        intent.putExtra(Constants.GROUP_POSITION,groupPosition);
        intent.putExtra(Constants.CHILD_POSITION,childPosition);
        fragment.startActivityForResult(intent,requestCode);
    }
    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_mapping_data_detail;
    }

    @Override
    protected void initChildViews() {
        mMapView= (TextureMapView) findViewById(R.id.activity_mapping_data_detail_map_view);
        tv_address= (TextView) findViewById(R.id.activity_mapping_data_detail_tv_address);
        tv_area= (TextView) findViewById(R.id.activity_mapping_data_detail_tv_area);
        tv_comments= (TextView) findViewById(R.id.activity_mapping_data_detail_tv_comments);
        tv_locCount= (TextView) findViewById(R.id.activity_mapping_data_detail_tv_loc_counts);
        tv_pathLength= (TextView) findViewById(R.id.activity_mapping_data_detail_tv_path_length);
        tv_perimeter= (TextView) findViewById(R.id.activity_mapping_data_detail_tv_perimeter);
        tv_tagName= (TextView) findViewById(R.id.activity_mapping_data_detail_tv_tag_name);
        tv_title= (TextView) findViewById(R.id.activity_mapping_data_detail_tv_title);
        mListView= (FullLengthListView) findViewById(R.id.activity_mapping_data_detail_list_view);
        rl_noPic= (RelativeLayout) findViewById(R.id.activity_mapping_data_detail_rl_no_pic);
        mBaiduMap=mMapView.getMap();
        mToolbar= (Toolbar) findViewById(R.id.activity_mapping_data_detail_tool_bar);
        mCollapsingToolbarLayout= (CollapsingToolbarLayout) findViewById(R.id.activity_mapping_data_detail_collapsing_toolbar_layout);
        mAppBarLayout= (AppBarLayout) findViewById(R.id.activity_mapping_data_detail_app_bar_layout);

    }

    @Override
    protected void initData() {
        mMapView.setLogoPosition(LogoPosition.logoPostionRightBottom);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if(mActionBar!=null){
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
        }
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.BLACK);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.TextAppearanceExpandedTitle);
        int margin=getResources().getDimensionPixelSize(R.dimen.margin_tool_bar_expanded_title_margin);
        mCollapsingToolbarLayout.setExpandedTitleMargin(margin,margin,margin,margin);

        mIntent = getIntent();
        if(mIntent !=null){
            MappingData data= mIntent.getParcelableExtra(Constants.MAPPING_DATA_SOURCE);
            if(data!=null){
                mMappingData = data;
                populateUiWithExistingData(mMappingData);
            }
        }
    }

    @Override
    protected void initListeners() {
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

        mAppBarLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mAppBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mCollpasePoint = mCollapsingToolbarLayout.getMeasuredHeight() - mToolbar.getMeasuredHeight();
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if(Math.abs(verticalOffset)==mCollpasePoint){
                    shouldExpand =false;
                }else {
                    shouldExpand =true;
                }
                if(shouldExpand != isExpanded){
                    isExpanded=shouldExpand;
                    supportInvalidateOptionsMenu();
                }
            }
        });
    }

    private void populateUiWithExistingData(MappingData data) {
        String title = data.getTitle();
        if(title.length()>13){
            mCollapsingToolbarLayout.setTitle(String.copyValueOf(title.toCharArray(),0,13)+"...");
        }else {
            mCollapsingToolbarLayout.setTitle(title);
        }
        tv_title.setText(title);
        tv_tagName.setText(data.getTagName());
        tv_perimeter.setText(String.format("%.2f",data.getPerimeter())+" m");
        tv_pathLength.setText(String.format("%.2f",data.getPathLength())+" m");
        tv_area.setText(String.format("%.2f",data.getArea())+" ㎡");
        tv_address.setText(data.getAddress());
        tv_comments.setText(data.getComment());
        tv_locCount.setText(String.valueOf(data.getLatLngs().size())+" 个");

        drawLayerWithCoordinates(data.getLatLngs());
        populateListView(data.getPicPaths());
    }

    private void populateListView(ArrayList<String> picPaths) {
        if(picPaths.size()>0){
            rl_noPic.setVisibility(View.GONE);
        }else {
            rl_noPic.setVisibility(View.VISIBLE);
        }
        mAdapter=new MappingDetailPicListAdapter(picPaths,this);
        mListView.setAdapter(mAdapter);
        mListView.setDividerHeight(0);
    }

    private void drawLayerWithCoordinates(ArrayList<MyLatLng> coords){
        ArrayList<LatLng> latLngs=new ArrayList<>();
        for(MyLatLng temp:coords){
            latLngs.add(new LatLng(temp.getLat(),temp.getLng()));
        }
        int size = latLngs.size();
        mBaiduMap.clear();
        //跳转
        double centerLat=0;
        double centerLng=0;
        for(MyLatLng temp:coords){
            centerLat+=temp.getLat();
            centerLng+=temp.getLng();
        }
        if(size>0){
            centerLat=centerLat/size;
            centerLng=centerLng/size;
        }
        MapUtil.goToLocation(mBaiduMap,centerLat,centerLng,0,20);
        //画点
        for(int i=0;i<size;i++){
            LatLng temp = latLngs.get(i);
            MediumMarkerView view=new MediumMarkerView(this,String.format("%02d",i+1));
            MarkerOptions markerOptions=new MarkerOptions()
                    .anchor(0.5f,0.5f)
                    .icon(BitmapDescriptorFactory.fromView(view))
                    .draggable(false)
                    .position(temp)
                    .animateType(MarkerOptions.MarkerAnimateType.grow);
            mBaiduMap.addOverlay(markerOptions);
        }
        //画域
        if(size>2){
            PolygonOptions polygonOptions=new PolygonOptions()
                    .points(latLngs)
                    .fillColor(getResources().getColor(R.color.colorSkyBlueLight));
            mBaiduMap.addOverlay(polygonOptions);
        }
        //画线
        if(size>1){
            PolylineOptions polylineOptions=new PolylineOptions()
                    .color(Color.RED)
                    .points(latLngs)
                    .width(5);
            mBaiduMap.addOverlay(polylineOptions);

            if(size>2){
                ArrayList<LatLng> list=new ArrayList<>();
                list.add(latLngs.get(0));
                list.add(latLngs.get(size-1));
                PolylineOptions closePerimeterLine=new PolylineOptions()
                        .color(Color.BLUE)
                        .dottedLine(true)
                        .points(list)
                        .width(5);
                mBaiduMap.addOverlay(closePerimeterLine);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_loc_details,menu);
        MenuItem itemEdit = menu.findItem(R.id.menu_loc_detail_edit);
        if(isExpanded){
            itemEdit.setIcon(R.drawable.selector_ic_edit_location_grey_to_white);
            if(mActionBar!=null){
                mActionBar.setHomeAsUpIndicator(R.drawable.selector_back_grey_to_white);
            }
        }else {
            itemEdit.setIcon(R.drawable.selector_ic_edit_location_white_to_grey);
            if(mActionBar!=null){
                mActionBar.setHomeAsUpIndicator(null);
            }
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
                if(mIntent!=null){
                    SaveMappingDataActivity.startForResult(this,mIntent,REQUEST_CODE_EDIT_DATA);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_EDIT_DATA){
            switch (resultCode){
                case RESULT_OK:
                    setResult(RESULT_OK,data);
                    MappingData mappingData=data.getParcelableExtra(Constants.MAPPING_DATA_SAVED);
                    if(mappingData!=null){
                        populateUiWithExistingData(mappingData);
                    }
                    break;
                case RESULT_CANCELED:
                    setResult(RESULT_CANCELED);
                    break;
            }
        }
    }
}
