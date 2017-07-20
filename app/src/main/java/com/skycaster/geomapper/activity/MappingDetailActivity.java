package com.skycaster.geomapper.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.MappingDetailPicListAdapter;
import com.skycaster.geomapper.base.BaseActivity;
import com.skycaster.geomapper.bean.MappingData;
import com.skycaster.geomapper.bean.MyLatLng;
import com.skycaster.geomapper.customized.FullLengthListView;
import com.skycaster.geomapper.data.Constants;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/20.
 */

public class MappingDetailActivity extends BaseActivity {

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

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if(intent!=null){
            MappingData data=intent.getParcelableExtra(Constants.MAPPING_DATA_SOURCE);
            if(data!=null){
                mMappingData = data;
                populateUiWithExistingData(mMappingData);
            }
        }


    }

    @Override
    protected void initListeners() {

    }

    private void populateUiWithExistingData(MappingData data) {
        tv_tagName.setText(data.getTagName());
        tv_title.setText(data.getTitle());
        tv_perimeter.setText(String.format("%.2f",data.getPerimeter())+" m");
        tv_pathLength.setText(String.format("%.2f",data.getPathLength())+" m");
        tv_area.setText(String.format("%.2f",data.getArea())+" ㎡");
        tv_address.setText(data.getAddress());
        tv_comments.setText(data.getComment());
        tv_locCount.setText(String.valueOf(data.getLatLngs().size())+" 个");

        mAdapter=new MappingDetailPicListAdapter(data.getPicPaths(),this);
        mListView.setAdapter(mAdapter);

        drawLayerWithCoordinates(data.getLatLngs());
    }

    private void drawLayerWithCoordinates(ArrayList<MyLatLng> coords){
        ArrayList<LatLng> latLngs=new ArrayList<>();
        for(MyLatLng temp:coords){
            latLngs.add(new LatLng(temp.getLat(),temp.getLng()));
        }
        int size = latLngs.size();

    }


}
