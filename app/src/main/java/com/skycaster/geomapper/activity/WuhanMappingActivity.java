package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextSwitcher;
import android.widget.ToggleButton;

import com.baidu.mapapi.map.TextureMapView;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.customized.LanternView;
import com.skycaster.geomapper.customized.MapTypeSelector;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.presenters.WuHanMappingPresenter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 廖华凯 on 2017/8/14.
 */

public class WuhanMappingActivity extends BaseActionBarActivity {
    private TextureMapView mMapView;
    private MapTypeSelector mMapTypeSelector;
    private WuHanMappingPresenter mPresenter;
    private TextSwitcher mTextSwitcher;
    private AtomicBoolean isInNaviMode=new AtomicBoolean(false);
    private LanternView mLanternView;
    private ToggleButton tgbtn_autoSaveGpggaData;
    private AtomicBoolean isAutoSaveGpggaData=new AtomicBoolean(false);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
        mTextSwitcher= (TextSwitcher) findViewById(R.id.activity_wuhan_text_switcher);
        mLanternView= (LanternView) findViewById(R.id.activity_wuhan_lantern_view);
        tgbtn_autoSaveGpggaData = (ToggleButton) findViewById(R.id.activity_wuhan_map_toggle_button_auto_save);
    }

    @Override
    protected void initRegularData() {
        isInNaviMode.compareAndSet(false,BaseApplication.getSharedPreferences().getBoolean(StaticData.NAVI_MODE, false));
        mPresenter=new WuHanMappingPresenter(this);
        mPresenter.initData();

    }

    @Override
    protected void initListeners() {
        tgbtn_autoSaveGpggaData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mPresenter.createNewGpggaRecord();
                }else {
                    mPresenter.closeGpggaRecord();
                }
                isAutoSaveGpggaData.set(isChecked);
            }
        });

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

    public TextSwitcher getTextSwitcher() {
        return mTextSwitcher;
    }

    public AtomicBoolean getIsInNaviMode() {
        return isInNaviMode;
    }

    public LanternView getLanternView() {
        return mLanternView;
    }

    public AtomicBoolean getIsAutoSaveGpggaData() {
        return isAutoSaveGpggaData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wuhan_map_activity,menu);
        MenuItem naviItem = menu.findItem(R.id.menu_wuhan_map_activity_ic_toogle_navi_mode);
        if(isInNaviMode.get()){
            naviItem.setIcon(R.drawable.ic_navi_mode_on);
        }else {
            naviItem.setIcon(R.drawable.ic_navi_mode_off);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_wuhan_map_activity_ic_clear_trace:
                mPresenter.confirmClearTrace();
                break;
            case R.id.menu_wuhan_map_activity_ic_toogle_navi_mode:
                isInNaviMode.set(!isInNaviMode.get());
                BaseApplication.getSharedPreferences().edit().putBoolean(StaticData.NAVI_MODE,isInNaviMode.get()).apply();
                supportInvalidateOptionsMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
