package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActivity;
import com.skycaster.geomapper.service.PortDataBroadcastingService;
import com.skycaster.geomapper.util.AlertDialogUtil;

public class TabActivity extends BaseActivity {



    public static void startActivity(Context context){
        context.startActivity(new Intent(context,TabActivity.class));
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_tab;
    }

    @Override
    protected void initChildViews() {


    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PortDataBroadcastingService.getSerialPort()==null){
            AlertDialogUtil.showHint(
                    this,
                    getString(R.string.prompt_to_set_serial_port),
                    new Runnable() {
                        @Override
                        public void run() {
                            SerialPortAdminActivity.start(TabActivity.this);
                        }
                    },
                    new Runnable() {
                        @Override
                        public void run() {

                        }
                    }
            );

        }
    }

    @Override
    protected void initListeners() {

    }

    public void startTraceActivity(View view) {
        BaiduTraceActivity.startActivity(this);
    }

    public void toSystemSetting(View view) {SettingsActivity.startActivity(this);}
}
