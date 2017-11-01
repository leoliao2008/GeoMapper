package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.data.TagType;

public class SettingsActivity extends BaseActionBarActivity {


    public static void start(Context context){
        context.startActivity(new Intent(context,SettingsActivity.class));
    }

    @Override
    protected String setActionBarTitle() {
        return getResources().getString(R.string.system_setting);
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initChildViews() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListeners() {

    }


    public void toOffLineMapSetting(View view) {
        OffLineMapAdminActivity.startActivity(this);
    }

    public void toSerialPortSetting(View view) {
        BeidouSetting.start(this);
    }

    public void toLocationTagAdmin(View view) {
        TagAdminActivity.start(this, TagType.TAG_TYPE_LOC);
    }

    public void toMappingDataTagAdmin(View view) {
        TagAdminActivity.start(this,TagType.TAG_TYPE_MAPPING_DATA);
    }

    public void toAboutUs(View view) {
        AboutUsActivity.start(this);
    }

    public void toCDRadioSetting(View view) {
        CDRadioSetting.start(this);
    }
}
