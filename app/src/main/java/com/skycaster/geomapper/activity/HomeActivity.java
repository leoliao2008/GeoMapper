package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActivity;

public class HomeActivity extends BaseActivity {



    public static void startActivity(Context context){
        context.startActivity(new Intent(context,HomeActivity.class));
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_home;
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
