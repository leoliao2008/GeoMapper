package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.data.TagType;

public class SettingsActivity extends BaseActionBarActivity {

    private TextView tv_appVersion;

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
        tv_appVersion= (TextView) findViewById(R.id.tv_version_code);

    }

    @Override
    protected void initData() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES | PackageManager.GET_CONFIGURATIONS);
            tv_appVersion.setText("当前软件版本："+info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            tv_appVersion.setText("获取软件版本失败。");
        }
    }

    @Override
    protected void initListeners() {

    }


    public void toOffLineMapSetting(View view) {
        OffLineMapAdminActivity.startActivity(this);
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



    public void toSK9042Setting(View view) {
        SK9042SettingActivity.start(this);
    }

    public void toGPSSetting(View view) {
        GPSSettingActivity.start(this);
    }
}
