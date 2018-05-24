package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.ToggleButton;

import com.baidu.mapapi.map.TextureMapView;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.customized.LanternView;
import com.skycaster.geomapper.customized.MapTypeSelector;
import com.skycaster.geomapper.customized.MappingResultPanel;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.presenters.MappingActivityPresenter;
import com.skycaster.geomapper.util.LogUtil;
import com.skycaster.geomapper.util.ToastUtil;

import butterknife.BindView;

/**
 * 最新的测绘界面，今后主要使用这个版本。
 */
public class MappingActivity extends BaseActionBarActivity {


    @BindView(R.id.activity_mapping_map_view)
    TextureMapView mMapView;
    @BindView(R.id.activity_mapping_map_type_selector)
    MapTypeSelector mMapTypeSelector;
    @BindView(R.id.activity_mapping_lantern_view)
    LanternView mLanternView;
    @BindView(R.id.activity_mapping_txt_switcher)
    TextSwitcher mTxtSwitcher;
    @BindView(R.id.activity_mapping_result_panel)
    MappingResultPanel mMappingResultPanel;
    @BindView(R.id.activity_mapping_tgbtn_test)
    ToggleButton mTgbtnTest;

    private MappingActivityPresenter mPresenter;

    private boolean isInNaviMode;
    private boolean isSaveGpggaData;
    private SharedPreferences mSharedPreferences;


    /*******BUG退散*********BUG退散***********主体内容************BUG退散************BUG退散*********BUG退散*/


    public static void start(Context context) {
        Intent starter = new Intent(context, MappingActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_mapping;
    }

    @Override
    protected void initChildViews() {

    }

    @Override
    protected void initListeners() {

    }


    @Override
    protected String setActionBarTitle() {
        return null;
    }

    protected void initData() {
        mSharedPreferences = BaseApplication.getSharedPreferences();
        isInNaviMode = mSharedPreferences.getBoolean(StaticData.IS_IN_NAVI_MODE, false);
        mPresenter = new MappingActivityPresenter(this);
        mPresenter.init();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mapping_activity, menu);
        MenuItem itemStickToCur = menu.findItem(R.id.menu_mapping_stick_to_current_location);
        //根据当前是否导航模式决定图标款式
        if (isInNaviMode) {
            itemStickToCur.setIcon(R.drawable.selector_ic_navigate_route_white_to_grey);
        } else {
            itemStickToCur.setIcon(R.drawable.selector_ic_navigate_route_grey_to_white);
        }
        //根据当前是否正在保存GNGGA数据决定title文字表述
        MenuItem itemSaveGngga = menu.findItem(R.id.menu_mapping_is_save_gpgga_data);
        if (isSaveGpggaData) {
            itemSaveGngga.setTitle("停止保存定位数据");
        } else {
            itemSaveGngga.setTitle("开始保存定位数据");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_mapping_stick_to_current_location://进入追踪模式
                isInNaviMode = !isInNaviMode;
                mSharedPreferences.edit().putBoolean(StaticData.IS_IN_NAVI_MODE, isInNaviMode).apply();
                supportInvalidateOptionsMenu();
                break;
            case R.id.menu_mapping_record_current_location://记录当前位置
                mPresenter.saveCurrentLocation();
                break;
//            case R.id.menu_mapping_show_history_routes:
//                mPresenter.displayHistoryRoutes();//此功能有点多余，去掉算了。
//                break;
            case R.id.menu_mapping_activate_mapping_mode://进入测绘模式
                mPresenter.activateMappingMode();
                break;
            case R.id.menu_mapping_is_save_gpgga_data://保存GPGGA裸数据到本地
                if (isSaveGpggaData) {
                    mPresenter.stopRecordingGNGGP();
                    showToast("已停止保存定位数据。");
                } else {
                    mPresenter.startRecordingGNGGP();
                    showToast("开始保存定位数据。");
                }
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
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
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    public TextureMapView getMapView() {
        return mMapView;
    }

    public MapTypeSelector getMapTypeSelector() {
        return mMapTypeSelector;
    }

    public LanternView getLanternView() {
        return mLanternView;
    }

    public TextSwitcher getTxtSwitcher() {
        return mTxtSwitcher;
    }

    public boolean isInNaviMode() {
        return isInNaviMode;
    }

    public boolean isSaveGpggaData() {
        return isSaveGpggaData;
    }

    public void setSaveGpggaData(boolean saveGpggaData) {
        isSaveGpggaData = saveGpggaData;
    }

    public MappingResultPanel getMappingResultPanel() {
        return mMappingResultPanel;
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(msg);
            }
        });
    }



    public void showLog(String msg) {
        LogUtil.showLog(getClass().getSimpleName(), msg);
    }

    public void Test(View view) {
        if(mTgbtnTest.isChecked()){
            mPresenter.startTest();
        }else {
            mPresenter.stopTest();
        }

    }
}
