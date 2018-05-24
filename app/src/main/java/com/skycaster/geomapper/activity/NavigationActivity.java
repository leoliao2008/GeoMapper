package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActivity;

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
    protected void initBaseData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        showLog(metrics.toString());

        //暂时增加此功能
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //暂时不需要此功能
//        if(BeidouDataBroadcastingService.getSerialPort()==null){
//            AlertDialogUtil.showStandardDialog(
//                    this,
//                    getString(R.string.prompt_to_set_serial_port),
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            GPSSetting.start(NavigationActivity.this);
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
        MappingActivity.start(this);

    }

    public void toSystemSetting(View view) {SettingsActivity.start(this);}


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


}
