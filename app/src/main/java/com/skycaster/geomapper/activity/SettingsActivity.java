package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;

public class SettingsActivity extends BaseActionBarActivity {


    public static void start(Context context){
        context.startActivity(new Intent(context,SettingsActivity.class));
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.system_setting;
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initChildViews() {

    }

    @Override
    protected void initRegularData() {

    }

    @Override
    protected void initListeners() {

    }


    public void toOffLineMapSetting(View view) {
        OffLineMapAdminActivity.startActivity(this);
    }

    public void toSerialPortSetting(View view) { SerialPortAdminActivity.start(this);
    }
}
