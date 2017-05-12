package com.skycaster.geomapper.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.skycaster.geomapper.R;
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
        mBaseApplication= (BaseApplication) getApplication();
        mBaseApplication.addToStack(this);
        TAG=getClass().getSimpleName();
        setContentView(setBaseLayout());
        initView();
        initData();
        initListener();
    }

    protected abstract int setBaseLayout();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

    protected void showToast(String msg){
        ToastUtil.showToast(msg);
    }

    protected void showLog(String msg){
        LogUtil.showLog(TAG,msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBaseApplication.getActivitiesCount()==1){
            AlertDialogUtil.showHint(this, getString(R.string.confirm_exit), new Runnable() {
                @Override
                public void run() {
                    mBaseApplication.removeFromStack(BaseActivity.this);
                    finish();
                }
            }, new Runnable() {
                @Override
                public void run() {
                    //do nothing
                }
            });
        }else {
            mBaseApplication.removeFromStack(this);
        }
    }
}
