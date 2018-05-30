package com.skycaster.geomapper.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.service.GPSDataBroadcastingService;
import com.skycaster.geomapper.util.AlertDialogUtil;
import com.skycaster.geomapper.util.LogUtil;
import com.skycaster.geomapper.util.ToastUtil;

import butterknife.ButterKnife;

/**
 * Created by 廖华凯 on 2017/5/12.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private String TAG;
    private BaseApplication mBaseApplication;
    private DisplayMetrics mDisplayMetrics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        BaseApplication.setDisplayMetrics(mDisplayMetrics);
        mBaseApplication= (BaseApplication) getApplication();
        mBaseApplication.addToStack(this);
        TAG=getClass().getSimpleName();
        setContentView(setRootViewLayout());
        ButterKnife.bind(this);
        initChildViews();
        initBaseData();
        initListeners();
    }

    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    public BaseApplication getBaseApplication() {
        return mBaseApplication;
    }

    protected abstract int setRootViewLayout();

    protected abstract void initChildViews();

    protected abstract void initBaseData();

    protected abstract void initListeners();

    protected void showToast(String msg){
        ToastUtil.showToast(msg);
    }

    protected void showLog(String msg){
        LogUtil.showLog(TAG,msg);
    }

    @Override
    public void onBackPressed() {
        if(mBaseApplication.getActivitiesCount()==1){
            AlertDialogUtil.showStandardDialog(this, getString(R.string.confirm_exit), new Runnable() {
                @Override
                public void run() {
                    //退出前台服务，停止广播，关闭SK9042以及GPS模块的电源。这些逻辑都在服务里封装好了。
                    stopService(new Intent(BaseActivity.this, GPSDataBroadcastingService.class));
                    BaseActivity.super.onBackPressed();
                }
            }, new Runnable() {
                @Override
                public void run() {
                    //do nothing
                }
            });
        }else {
            BaseActivity.super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        mBaseApplication.removeFromStack(this);
        super.finish();
    }

    protected void onclick(int viewId, View.OnClickListener listener){
        findViewById(viewId).setOnClickListener(listener);
    }
}
