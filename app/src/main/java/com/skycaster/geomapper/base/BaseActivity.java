package com.skycaster.geomapper.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.service.PortDataBroadcastingService;
import com.skycaster.geomapper.util.AlertDialogUtil;
import com.skycaster.geomapper.util.LogUtil;
import com.skycaster.geomapper.util.ToastUtil;

/**
 * Created by 廖华凯 on 2017/5/12.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private String TAG;
    private BaseApplication mBaseApplication;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        BaseApplication.setDisplayMetrics(metrics);
        mBaseApplication= (BaseApplication) getApplication();
        mBaseApplication.addToStack(this);
        TAG=getClass().getSimpleName();
        setContentView(setRootViewLayout());
        initChildViews();
        initData();
        initListeners();
    }

    protected abstract int setRootViewLayout();

    protected abstract void initChildViews();

    protected abstract void initData();

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
            AlertDialogUtil.showHint(this, getString(R.string.confirm_exit), new Runnable() {
                @Override
                public void run() {
                    stopService(new Intent(BaseActivity.this, PortDataBroadcastingService.class));
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

    protected void attachOnclick(int viewId, View.OnClickListener listener){
        findViewById(viewId).setOnClickListener(listener);
    }
}
