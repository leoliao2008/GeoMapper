package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextSwitcher;

import com.baidu.mapapi.map.TextureMapView;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.customized.LanternView;
import com.skycaster.geomapper.customized.MapTypeSelector;
import com.skycaster.geomapper.customized.MappingResultPanel;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.presenters.MappingActivityPresenter;
import com.skycaster.geomapper.util.LogUtil;
import com.skycaster.geomapper.util.ToastUtil;
import com.skycaster.skycaster21489.abstr.AckCallBack;
import com.skycaster.skycaster21489.base.AdspActivity;
import com.skycaster.skycaster21489.data.ServiceCode;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 最新的测绘界面，今后主要使用这个版本。
 */
public class MappingActivity extends AdspActivity {


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
    private MappingActivityPresenter mPresenter;
    private AckCallBack mAckCallBack = new AckCallBack(this) {
        @Override
        public void onError(String s) {
            showHint(s);
        }

        @Override
        public void activate(final boolean b, final String s) {
            super.activate(b, s);
            showLog(s);
            if (b) {
                showLog("启动裸数据传输。");
                getRequestManager().startService(ServiceCode.RAW_DATA);
            } else {
                showToast(s);
            }
        }

        @Override
        public void deactivate(boolean b, String s) {
            super.deactivate(b, s);
            showLog(s);
        }

        @Override
        public void startService(boolean b, ServiceCode serviceCode) {
            super.startService(b, serviceCode);
            if (b) {
                showToast("基合定位成功启动。");
                showLog("裸数据传输启动。");
                BaseApplication.postDelay(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showLog("开始切换GPIO 连接cd radio 和北斗");
                            mPresenter.connectCdRadioToBeidou();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);
            } else {
                showToast("基合定位启动失败。");
                showLog("基合定位启动失败。");
            }
        }
    };
    private boolean isInNaviMode;
    private boolean isSaveGpggaData;
    private SharedPreferences mSharedPreferences;


    /*******BUG退散*********BUG退散***********主体内容************BUG退散************BUG退散*********BUG退散*/


    public static void start(Context context) {
        Intent starter = new Intent(context, MappingActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_mapping);
        ButterKnife.bind(this);
        initData();
        initListeners();
    }

    @NonNull
    @Override
    protected AckCallBack setSerialPortAckCallBack() {
        return mAckCallBack;
    }

    @NonNull
    @Override
    protected String setDefaultSerialPortPath() {
        return StaticData.CD_RADIO_MODULE_SP_PATH;
    }

    @Override
    protected int setDefaultBaudRate() {
        return StaticData.CD_RADIO_MODULE_SP_BAUD_RATE;
    }

    @Override
    public void onGetRawData(byte[] bytes, int i) {
        //do nothing
    }

    protected void initData() {
        mSharedPreferences = getMySharedPreferences();
        isInNaviMode = mSharedPreferences.getBoolean(StaticData.IS_IN_NAVI_MODE, false);
        mPresenter = new MappingActivityPresenter(this);
        mPresenter.init();
    }


    protected void initListeners() {

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
            case R.id.menu_mapping_stick_to_current_location:
                isInNaviMode = !isInNaviMode;
                mSharedPreferences.edit().putBoolean(StaticData.IS_IN_NAVI_MODE, isInNaviMode).apply();
                supportInvalidateOptionsMenu();
                break;
            case R.id.menu_mapping_record_current_location:
                mPresenter.saveCurrentLocation();
                break;
//            case R.id.menu_mapping_show_history_routes:
//                mPresenter.displayHistoryRoutes();//此功能有点多余，去掉算了。
//                break;
            case R.id.menu_mapping_activate_mapping_mode:
                mPresenter.activateMappingMode();
                break;
            case R.id.menu_mapping_is_save_gpgga_data:
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
        mPresenter.onActivityResult(requestCode,resultCode,data);
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


    @Override
    public void showHint(String s) {
        showLog(s);
    }

    public void showLog(String msg) {
        LogUtil.showLog(getClass().getSimpleName(), msg);
    }
}
