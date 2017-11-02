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
import com.skycaster.geomapper.customized.LanternView;
import com.skycaster.geomapper.customized.MapTypeSelector;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.presenters.MappingActivityPresenter;
import com.skycaster.geomapper.util.LogUtil;
import com.skycaster.geomapper.util.ToastUtil;
import com.skycaster.skycaster21489.abstr.AckCallBack;
import com.skycaster.skycaster21489.base.AdspActivity;
import com.skycaster.skycaster21489.data.ServiceCode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MappingActivity extends AdspActivity {


    @BindView(R.id.activity_mapping_map_view)
    TextureMapView mMapView;
    @BindView(R.id.activity_mapping_map_type_selector)
    MapTypeSelector mMapTypeSelector;
    @BindView(R.id.activity_mapping_lantern_view)
    LanternView mLanternView;
    @BindView(R.id.activity_mapping_txt_switcher)
    TextSwitcher mTxtSwitcher;
    private MappingActivityPresenter mPresenter;
    private AckCallBack mAckCallBack = new AckCallBack(this) {
        @Override
        public void onError(String s) {
            showHint(s);
        }

        @Override
        public void activate(boolean b, String s) {
            showHint(s);
            if(b){
                getRequestManager().startService(ServiceCode.RAW_DATA);
            }else {
                showToast(s);
            }
        }

        @Override
        public void deactivate(boolean b, String s) {
            super.deactivate(b, s);
            showHint(s);
        }

        @Override
        public void startService(boolean b, ServiceCode serviceCode) {
            if(b){
                showToast("基合定位成功启动。");
            }else {
                showToast("基合定位启动失败。");
            }
        }
    };
    private boolean isInNaviMode;
    private SharedPreferences mSharedPreferences;


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
        mSharedPreferences=getMySharedPreferences();
        isInNaviMode=mSharedPreferences.getBoolean(StaticData.IS_IN_NAVI_MODE,false);
        mPresenter = new MappingActivityPresenter(this);
        mPresenter.init();
    }


    protected void initListeners() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mapping_activity,menu);
        MenuItem itemStickToCur = menu.findItem(R.id.menu_mapping_stick_to_current_location);
        //根据当前是否导航模式决定图标款式
        if(isInNaviMode){
            itemStickToCur.setIcon(R.drawable.selector_ic_navigate_route_white_to_grey);
        }else {
            itemStickToCur.setIcon(R.drawable.selector_ic_navigate_route_grey_to_white);
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
                isInNaviMode=!isInNaviMode;
                mSharedPreferences.edit().putBoolean(StaticData.IS_IN_NAVI_MODE, isInNaviMode).apply();
                supportInvalidateOptionsMenu();
                break;
            case R.id.menu_mapping_record_current_location:
                mPresenter.saveCurrentLocation();
                break;
            case R.id.menu_mapping_show_history_routes:
                mPresenter.displayHistoryRoutes();
                break;
            case R.id.menu_mapping_activate_mapping_mode:
                mPresenter.activateMappingMode();
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
        LogUtil.showLog(getClass().getSimpleName(),s);
    }
}
