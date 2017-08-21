package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.presenters.BluetoothSettingPresenter;

/**
 * Created by 廖华凯 on 2017/8/15.
 */

public class BluetoothSettingActivity extends BaseActionBarActivity {
    private BluetoothSettingPresenter mPresenter;
    private ListView mDeviceList;
    private ListView mDataList;
    private Button btn_requestStart;
//    private Button btn_requestStop;

    public static void start(Context context) {
        Intent starter = new Intent(context, BluetoothSettingActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.bluetooth_setting;
    }

    @Override
    protected void initRegularData() {
        mPresenter=new BluetoothSettingPresenter(this);
        mPresenter.initData();
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_blue_tooth_setting;
    }

    @Override
    protected void initChildViews() {
        mDeviceList = (ListView) findViewById(R.id.activity_blue_tooth_setting_device_list);
        mDataList= (ListView) findViewById(R.id.activity_blue_tooth_setting_data_list);
        btn_requestStart= (Button) findViewById(R.id.activity_blue_tooth_setting_btn_request_start);
//        btn_requestStop= (Button) findViewById(R.id.activity_blue_tooth_setting_btn_request_stop);
    }

    @Override
    protected void initListeners() {
        btn_requestStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.requestStartGpggaTransmission();
            }
        });

//        btn_requestStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPresenter.requestStopGpggaTransmission();
//            }
//        });

    }

    public ListView getDeviceList() {
        return mDeviceList;
    }

    public ListView getDataList() {
        return mDataList;
    }

    public Button getBtn_requestStart() {
        return btn_requestStart;
    }

//    public Button getBtn_requestStop() {
//        return btn_requestStop;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bluetooth_setting,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_bluetooth_searching_ic_search:
                mPresenter.searchDevices();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode,resultCode);
    }
}
