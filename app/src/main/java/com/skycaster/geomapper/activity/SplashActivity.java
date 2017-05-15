package com.skycaster.geomapper.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActivity;
import com.skycaster.geomapper.base.BaseApplication;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setBaseLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        BaseApplication.postDelay(new Runnable() {
            @Override
            public void run() {
                HomeActivity.startActivity(SplashActivity.this);
                finish();
            }
        },10);

    }

    @Override
    protected void initListener() {

    }


    static {
        System.loadLibrary("native-lib");
    }
}
