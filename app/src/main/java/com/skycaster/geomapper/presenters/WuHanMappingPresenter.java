package com.skycaster.geomapper.presenters;

import com.baidu.mapapi.map.TextureMapView;
import com.skycaster.geomapper.activity.WuhanMappingActivity;
import com.skycaster.geomapper.customized.MapTypeSelector;
import com.skycaster.geomapper.models.BaiduMapModel;

/**
 * Created by 廖华凯 on 2017/8/15.
 */

public class WuHanMappingPresenter {
    private WuhanMappingActivity mActivity;
    private TextureMapView mMapView;
    private MapTypeSelector mMapTypeSelector;
    private BaiduMapModel mMapModel;

    public WuHanMappingPresenter(WuhanMappingActivity activity) {
        mActivity = activity;
        mMapView=mActivity.getMapView();
        mMapTypeSelector=mActivity.getMapTypeSelector();
    }

    public void initData(){
        mMapTypeSelector.attachToMapView(mMapView);

    }

    public void onResume(){
        mMapView.onResume();
    }

    public void onPause(){
        mMapView.onPause();
    }

    public void onDestroy(){
        mMapView.onDestroy();
    }
}
