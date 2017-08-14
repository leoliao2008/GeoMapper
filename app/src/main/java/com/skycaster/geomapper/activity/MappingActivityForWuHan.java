package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.map.TextureMapView;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.customized.MapTypeSelector;

/**
 * Created by 廖华凯 on 2017/8/14.
 */

public class MappingActivityForWuHan extends BaseActionBarActivity {
    private TextureMapView mMapView;
    private MapTypeSelector mMapTypeSelector;

    public static void start(Context context) {
        Intent starter = new Intent(context, MappingActivityForWuHan.class);
        context.startActivity(starter);
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.basic_location_function;
    }

    @Override
    protected void initRegularData() {

    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_wuhan_senario;
    }

    @Override
    protected void initChildViews() {

    }

    @Override
    protected void initListeners() {

    }
}
