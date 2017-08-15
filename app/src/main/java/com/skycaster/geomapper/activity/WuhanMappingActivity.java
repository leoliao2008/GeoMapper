package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.map.TextureMapView;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.customized.MapTypeSelector;
import com.skycaster.geomapper.presenters.WuHanMappingPresenter;

/**
 * Created by 廖华凯 on 2017/8/14.
 */

public class WuhanMappingActivity extends BaseActionBarActivity {
    private TextureMapView mMapView;
    private MapTypeSelector mMapTypeSelector;
    private WuHanMappingPresenter mPresenter;

    public static void start(Context context) {
        Intent starter = new Intent(context, WuhanMappingActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.basic_location_function;
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_wuhan_senario;
    }

    @Override
    protected void initChildViews() {
        mMapView= (TextureMapView) findViewById(R.id.activity_wuhan_map_map_view);
        mMapTypeSelector= (MapTypeSelector) findViewById(R.id.activity_wuhan_map_type_selector);
    }

    @Override
    protected void initRegularData() {
        mPresenter=new WuHanMappingPresenter(this);
        mPresenter.initData();

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    public TextureMapView getMapView() {
        return mMapView;
    }

    public MapTypeSelector getMapTypeSelector() {
        return mMapTypeSelector;
    }
}
