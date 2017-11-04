package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.presenters.CDRadioSettingPresenter;
import com.skycaster.geomapper.util.LogUtil;
import com.skycaster.geomapper.util.ToastUtil;
import com.skycaster.skycaster21489.abstr.AckCallBack;
import com.skycaster.skycaster21489.base.AdspActivity;
import com.skycaster.skycaster21489.data.ServiceCode;

public class CDRadioSetting extends AdspActivity {
    private EditText mEdtFrq;
    private EditText mEdtLeftTune;
    private EditText mEdtRightTune;
    private TextView mTvPortPath;
    private TextView mTvPortRate;
    private ToggleButton mTgbtnActivateCdRadio;
    private CDRadioSettingPresenter mPresenter;
    private ListView mListView;
    private ToggleButton mTgbtnStartService;
    private boolean isCdRadioActivated;

    public static void start(Context context) {
        Intent starter = new Intent(context, CDRadioSetting.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_cdradio_setting);
        initActionBar();
        initViews();
        initData();
        initListeners();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("CdRadio模块参数设置");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    protected void initViews() {
        mTgbtnActivateCdRadio= (ToggleButton) findViewById(R.id.activity_cdradio_setting_toggle_btn_activate_cdradio);
        mEdtFrq= (EditText) findViewById(R.id.activity_cdradio_setting_edt_frq);
        mEdtLeftTune= (EditText) findViewById(R.id.activity_cdradio_setting_edt_left_tune);
        mEdtRightTune= (EditText) findViewById(R.id.activity_cdradio_setting_edt_right_tune);
        mTvPortPath= (TextView) findViewById(R.id.activity_cdradio_setting_tv_sp_path);
        mTvPortRate= (TextView) findViewById(R.id.activity_cdradio_setting_tv_sp_baud_rate);
        mListView= (ListView) findViewById(R.id.activity_cdradio_setting_recycler_view_data_console);
        mTgbtnStartService= (ToggleButton) findViewById(R.id.activity_cdradio_setting_toggle_btn_start_service);
    }

    protected void initListeners() {
        mTgbtnActivateCdRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTgbtnActivateCdRadio.isChecked()){
                    LogUtil.showLog(CDRadioSetting.this.getClass().getSimpleName(),"activate cd radio");
                    mPresenter.activateCdRadio();
                }else {
                    LogUtil.showLog(CDRadioSetting.this.getClass().getSimpleName(),"deactivate cd radio");
                    mPresenter.deactivateCdRadio();
                }
                mTgbtnActivateCdRadio.setChecked(!mTgbtnActivateCdRadio.isChecked());
            }
        });

        mTgbtnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTgbtnStartService.isChecked()){
                    mPresenter.startService();
                }else {
                    mPresenter.stopService();
                }
                mTgbtnStartService.setChecked(!mTgbtnStartService.isChecked());
            }
        });

    }

    protected void initData() {
        mPresenter=new CDRadioSettingPresenter(this);
        mPresenter.init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    public EditText getEdtFrq() {
        return mEdtFrq;
    }

    public EditText getEdtLeftTune() {
        return mEdtLeftTune;
    }

    public EditText getEdtRightTune() {
        return mEdtRightTune;
    }

    public TextView getTvPortPath() {
        return mTvPortPath;
    }

    public TextView getTvPortRate() {
        return mTvPortRate;
    }

    public ToggleButton getTgbtnActivateCdRadio() {
        return mTgbtnActivateCdRadio;
    }

    public ListView getRecyclerView() {
        return mListView;
    }

    public ToggleButton getTgbtnStartService() {
        return mTgbtnStartService;
    }

    public boolean isCdRadioActivated() {
        return isCdRadioActivated;
    }

    public void checkFrq(View view) {
        mPresenter.checkFrq();
    }

    public void checkLeftTune(View view) {
        mPresenter.checkTunes();
    }

    public void checkRightTune(View view) {
        mPresenter.checkTunes();
    }

    public void submitConfig(View view) {
        mPresenter.submitConfigs();
    }

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
                isCdRadioActivated=b;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getTgbtnActivateCdRadio().setChecked(b);
                        if(!TextUtils.isEmpty(s)){
                            showToast(s);
                        }
                    }
                });
            }

            @Override
            public void deactivate(final boolean b, final String s) {
                super.deactivate(b, s);
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(b){
                            isCdRadioActivated=false;
                            getTgbtnActivateCdRadio().setChecked(false);
                            getTgbtnStartService().setChecked(false);
                        }
                        if(!TextUtils.isEmpty(s)){
                            showToast(s);
                        }
                    }
                });
            }

            @Override
            public void checkFreq(final boolean b, final String s) {
                super.checkFreq(b, s);
                if(!b){
                    showToast(s);
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getEdtFrq().setText(s);
                            getEdtFrq().setSelection(s.length());
                        }
                    });
                }
            }

            @Override
            public void checkTunes(boolean b, final String s, final String s1) {
                super.checkTunes(b, s, s1);
                if(!b){
                    showToast(s);
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getEdtLeftTune().setText(s);
                            getEdtLeftTune().setSelection(s.length());
                            getEdtRightTune().setText(s1);
                            getEdtRightTune().setSelection(s1.length());
                        }
                    });
                }
            }

            @Override
            public void setFreq(boolean b, String s) {
                super.setFreq(b, s);
                showToast(s);
            }

            @Override
            public void setTuners(boolean b, String s) {
                super.setTuners(b, s);
                showToast(s);
            }

            @Override
            public void startService(final boolean b, ServiceCode serviceCode) {
                super.startService(b, serviceCode);
                if(!b){
                    showToast("CDRadio裸数据传输业务启动失败。");
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getTgbtnStartService().setChecked(b);
                        }
                    });
                    showToast("CDRadio裸数据传输业务启动启动。");
                }

            }

        };
    }

    @NonNull
    @Override
    protected String setDefaultSerialPortPath() {
        return "/dev/ttyS0";
    }

    @Override
    protected int setDefaultBaudRate() {
        return 115200;
    }

    @Override
    public void onGetRawData(byte[] bytes, int i) {
        try {
            mPresenter.onGetRawData(bytes,i);
        }catch (NullPointerException e){
            //do nothing
        }

    }

    @Override
    public void showHint(final String s) {
        super.showHint(s);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(CDRadioSetting.this,s,Toast.LENGTH_SHORT).show();
//            }
//        });
        LogUtil.showLog(getClass().getSimpleName(),s);
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
