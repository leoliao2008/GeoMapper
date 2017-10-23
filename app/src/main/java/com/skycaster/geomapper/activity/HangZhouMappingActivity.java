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
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.mapapi.map.TextureMapView;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.customized.LanternView;
import com.skycaster.geomapper.customized.MapTypeSelector;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.presenters.HangZhouMappingPresenter;
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
                showHint(s);

            }

            @Override
            public void activate(final boolean b, final String s) {
                super.activate(b, s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tgbtn_activateLocService.setChecked(b);
                        if(!b){
                            showHint(s);
                        }else {
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
                            isServiceRunning=true;
                            //把CDRadio的串口和北斗模块的串口连接起来。
                            mPresenter.connectCdRadioToBeidou();
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
                tgbtn_activateLocService.setChecked(!tgbtn_activateLocService.isChecked());
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
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    public void showHint(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HangZhouMappingActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
