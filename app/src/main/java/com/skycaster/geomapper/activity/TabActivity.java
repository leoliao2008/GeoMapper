package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActivity;

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
    protected void initListeners() {

    }

    public void startTraceActivity(View view) {
        BaiduTraceActivity.startActivity(this);
    }

    public void toSystemSetting(View view) {SettingsActivity.startActivity(this);}
}
