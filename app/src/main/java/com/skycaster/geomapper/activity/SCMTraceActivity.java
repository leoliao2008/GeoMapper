package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.map.TextureMapView;
import com.scgis.mmap.helper.TileCacheDBManager;
import com.scgis.mmap.map.SCGISTiledMapServiceLayer;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseMapActivity;

public class SCMTraceActivity extends BaseMapActivity {

    private String mDlgUrl;
    private String mToken;
    private TileCacheDBManager mTileCacheDBManager;
    private SCGISTiledMapServiceLayer mMapServiceLayer;
    private TextureMapView mMapView;

    public static void start(Context context) {
        Intent starter = new Intent(context, SCMTraceActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_scmtrace;
    }

    @Override
    protected void initChildViews() {
        mMapView= (TextureMapView) findViewById(R.id.activity_scm_trace_map_view);

    }

    @Override
    protected void initData() {
//        mDlgUrl = "http://www.scgis.net.cn/iMap/iMapServer/DefaultRest/services/newtianditudlg/";
//        mToken = " Ud1uC-jU0TmKiaeBPyog6cQBmwgWhlRllDICbePWmIYJRprioPA4ssNWeB-ZkLSn";
//        mTileCacheDBManager = new TileCacheDBManager(this,"adb.db");
//        mMapServiceLayer = new SCGISTiledMapServiceLayer(this,mDlgUrl,mToken,true,mTileCacheDBManager);
//        mMapServiceLayer.setCacheSize(100);
//        mMapServiceLayer.setTileCompressAndQuality(true,75);


    }

    @Override
    protected void initListeners() {

    }
}
