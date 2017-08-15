package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
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

    }

    @Override
    protected void initListeners() {

    }

    public ListView getDeviceList() {
        return mDeviceList;
    }

    public ListView getDataList() {
        return mDataList;
    }

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
