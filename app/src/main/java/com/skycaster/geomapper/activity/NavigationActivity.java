package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActivity;
import com.skycaster.geomapper.models.GPIOModel;

import java.io.IOException;

public class NavigationActivity extends BaseActivity {
    private GPIOModel mGPIOModel;


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
    protected void initBaseData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        showLog(metrics.toString());

        //打开北斗模块
        mGPIOModel=new GPIOModel();
        try {
            mGPIOModel.turnOnCdRadio();
        } catch (IOException e) {
            showLog(e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //暂时不需要此功能
//        if(BeidouDataBroadcastingService.getSerialPort()==null){
//            AlertDialogUtil.showHint(
//                    this,
//                    getString(R.string.prompt_to_set_serial_port),
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            BeidouSetting.start(NavigationActivity.this);
//                        }
//                    },
//                    new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    }
//            );
//
//        }
    }

    @Override
    protected void initListeners() {

    }

    public void startMappingActivity(View view) {
//        MappingActivityPre.start(this);
//        WuhanMappingActivity.start(this);

    }

    public void toSystemSetting(View view) {SettingsActivity.start(this);}

    public void startTrackingActivity(View view) {
    }

    public void toSatelliteMapActivity(View view) {
        SatelliteMapActivity.start(this);
    }



    public void toHistoryRecords(View view) {
        HistoryRecordsActivity.start(this);
    }

    public void toBluetoothSettingActivity(View view) {
        BluetoothSettingActivity.start(this);
    }

    public void toFileBrowser(View view) {
        FileBrowserActivity.start(this);
    }

    public void startLocActivity(View view) {
//        WuhanMappingActivity.start(this);
        HangZhouMappingActivity.start(this);
    }
}
