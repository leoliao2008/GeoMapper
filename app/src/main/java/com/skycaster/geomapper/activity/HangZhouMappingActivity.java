package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.ToggleButton;

import com.baidu.mapapi.map.TextureMapView;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.customized.LanternView;
import com.skycaster.geomapper.customized.MapTypeSelector;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.presenters.HangZhouMappingPresenter;
import com.skycaster.geomapper.util.LogUtil;
import com.skycaster.geomapper.util.ToastUtil;
import com.skycaster.skycaster21489.abstr.AckCallBack;
import com.skycaster.skycaster21489.base.AdspActivity;
import com.skycaster.skycaster21489.data.ServiceCode;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 廖华凯 on 2017/8/14.
 */

public class HangZhouMappingActivity extends AdspActivity {
    private TextureMapView mMapView;
    private MapTypeSelector mMapTypeSelector;
    private HangZhouMappingPresenter mPresenter;
    private TextSwitcher mTextSwitcher;
    private AtomicBoolean isInNaviMode=new AtomicBoolean(false);
    private LanternView mLanternView;
    private ToggleButton tgbtn_activateLocService;
    private boolean isServiceRunning;
    private boolean isSaveData=false;

    public static void start(Context context) {
        Intent starter = new Intent(context, HangZhouMappingActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wuhan_senario);
        initActionBar();
        initChildViews();
        initData();
        initListeners();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("基合定位");
        }
    }


    /**
     * CDRadio模块（21489模块）的命令发送后的回调。
     * @return
     */
    @NonNull
    @Override
    protected AckCallBack setSerialPortAckCallBack() {
        return new AckCallBack(this) {
            @Override
            public void onError(String s) {
                LogUtil.showLog(HangZhouMappingActivity.this.getClass().getSimpleName(),s);
            }

            @Override
            public void activate(final boolean b, final String s) {
                super.activate(b, s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tgbtn_activateLocService.setChecked(b);
                        if(!b){
                            showToast(s);
                        }else {
                            //启动CDRadio模块后，随即启动数据业务
                            getRequestManager().startService(ServiceCode.RAW_DATA);
                        }
                    }
                });
            }

            @Override
            public void deactivate(boolean b, String s) {
                super.deactivate(b, s);
                if(b){
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           tgbtn_activateLocService.setChecked(false);
                           isServiceRunning=false;
                       }
                   });
                }
            }

            @Override
            public void startService(final boolean b, ServiceCode serviceCode) {
                super.startService(b, serviceCode);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tgbtn_activateLocService.setChecked(b);
                        if(b){
                            //裸数据传输已经启动了
                            isServiceRunning=true;
                            //需要把CDRadio的串口和北斗模块的串口连接起来，把裸数据传给北斗模块
                            BaseApplication.postDelay(new Runnable() {
                                @Override
                                public void run() {
                                    mPresenter.connectCdRadioToBeidou();
                                }
                            },500);
                        }
                    }
                });
            }
        };
    }

    /**
     * CDRadio模块（21489模块）的串口路径
     * @return
     */
    @NonNull
    @Override
    protected String setDefaultSerialPortPath() {
        return "/dev/ttyS0";
    }

    /**
     * CDRadio模块（21489模块）的串口波特率
     * @return
     */
    @Override
    protected int setDefaultBaudRate() {
        return 115200;
    }


    private void initChildViews() {
        mMapView= (TextureMapView) findViewById(R.id.activity_wuhan_map_map_view);
        mMapTypeSelector= (MapTypeSelector) findViewById(R.id.activity_wuhan_map_type_selector);
        mTextSwitcher= (TextSwitcher) findViewById(R.id.activity_wuhan_text_switcher);
        mLanternView= (LanternView) findViewById(R.id.activity_wuhan_lantern_view);
        tgbtn_activateLocService = (ToggleButton) findViewById(R.id.activity_wuhan_map_toggle_button_auto_save);

    }

    private void initData() {
        tgbtn_activateLocService.setTextOn("基合定位已启动");
        tgbtn_activateLocService.setTextOff("基合定位未启动");
        tgbtn_activateLocService.setChecked(false);
        isInNaviMode.compareAndSet(false,BaseApplication.getSharedPreferences().getBoolean(StaticData.NAVI_MODE, false));
        mPresenter=new HangZhouMappingPresenter(this);
        mPresenter.init();
    }

    private void initListeners() {
        tgbtn_activateLocService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tgbtn_activateLocService.isChecked()){
                    mPresenter.startLocService();
                }else {
                    mPresenter.stopLocService();
                }
                tgbtn_activateLocService.setChecked(!tgbtn_activateLocService.isChecked());//根据回调结果再来改变状态
            }
        });

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
    public void onGetRawData(byte[] bytes, int i) {
        //由于CDRadio模块业务数据传输的过程中和主板的串口将被切断，裸数据被传输到北斗模块中去了，因此在app端的这个串口是接收不到裸数据的。
        //裸数据经北斗模块处理后，生成的明文数据将通过另一个和北斗模块接连的串口获得，并通过前台服务广播出去。
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

    public boolean isServiceRunning() {
        return isServiceRunning;
    }

    public boolean isSaveData() {
        return isSaveData;
    }

    public void setSaveData(boolean saveData) {
        isSaveData = saveData;
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
        MenuItem clearItem = menu.findItem(R.id.menu_wuhan_map_activity_ic_clear_trace);
        clearItem.setVisible(false);
        MenuItem saveItem = menu.findItem(R.id.menu_wuhan_map_activity_ic_save_data);
        if(isSaveData){
            saveItem.setTitle("停止保存数据");
        }else {
            saveItem.setTitle("开始保存数据");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
//            case R.id.menu_wuhan_map_activity_ic_clear_trace:
//                mPresenter.confirmClearTrace();
//                break;
            case R.id.menu_wuhan_map_activity_ic_toogle_navi_mode:
                isInNaviMode.set(!isInNaviMode.get());
                BaseApplication.getSharedPreferences().edit().putBoolean(StaticData.NAVI_MODE,isInNaviMode.get()).apply();
                supportInvalidateOptionsMenu();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_wuhan_map_activity_ic_save_data:
                if(!isSaveData()){
                    mPresenter.startRecordingData();
                }else {
                    mPresenter.stopRecordingData();
                }
                break;
        }
        return true;
    }

    public void showToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showToast(msg);
            }
        });
    }
}
