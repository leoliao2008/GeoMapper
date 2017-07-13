package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActivity;
import com.skycaster.geomapper.service.PortDataBroadcastingService;
import com.skycaster.geomapper.util.AlertDialogUtil;

public class NavigationActivity extends BaseActivity {



    public static void startActivity(Context context){
        context.startActivity(new Intent(context,NavigationActivity.class));
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_navigation;
    }

    @Override
    protected void initChildViews() {


    }

    @Override
    protected void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
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
                            SerialPortAdminActivity.start(NavigationActivity.this);
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
        MapActivity.start(this);
    }

    public void toSystemSetting(View view) {SettingsActivity.start(this);}

    public void startTrackingActivity(View view) {
    }

    public void toSatelliteMapActivity(View view) {
        SatelliteViewActivity.start(this);
    }



    public void toHistoryRecords(View view) {
        GeoRecordsActivity.start(this);
    }


    public void toTestActivity(View view) {
        startActivity(new Intent(this,SaveMappingDataActivity.class));
    }
}
