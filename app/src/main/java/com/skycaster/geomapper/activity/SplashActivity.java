package com.skycaster.geomapper.activity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.Window;

import com.baidu.mapapi.SDKInitializer;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.Constants;
import com.skycaster.geomapper.util.AlertDialogUtil;

public class SplashActivity extends BaseActivity {

    private static final int REQUEST_SYS_PERMISSIONS = 145;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //百度地图启动前需初始化
        SDKInitializer.initialize(BaseApplication.getContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_splash;
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

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(checkPermissions()){
            goToTabActivity();
        }else {
            requestSysPermissions();
        }
    }

    private void requestSysPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(Constants.SYS_PERMISSIONS,REQUEST_SYS_PERMISSIONS);
        }
    }

    private boolean checkPermissions() {
        boolean isGranted=true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for(String p: Constants.SYS_PERMISSIONS){
                if(PackageManager.PERMISSION_GRANTED!=checkSelfPermission(p)){
                    isGranted=false;
                    break;
                }
            }
        }
        return isGranted;
    }

    private void goToTabActivity() {
        TabActivity.startActivity(this);
        finish();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_SYS_PERMISSIONS){
            boolean isGranted=true;
            int len=grantResults.length;
            StringBuilder sb=new StringBuilder();
            sb.append("为了运行本程序的定位及读写数据功能，需要获得以下系统权限：\n");
            for(String p:Constants.SYS_PERMISSIONS){
                sb.append(p).append("\n");
            }
            for(int i=0;i<len;i++){
                if(PackageManager.PERMISSION_GRANTED!=grantResults[i]){
                    isGranted=false;
                    if(shouldShowRequestPermissionRationale(permissions[i])){
                        sb.append("点击确定重新申请以上权限，点击取消退出本程序。");
                        AlertDialogUtil.showHint(this, sb.toString(), new Runnable() {
                            @Override
                            public void run() {
                                requestSysPermissions();
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }else {
                        showLog(permissions[i]);
                        sb.append("您已经永久禁用了系统获取以上部分或全部权限，请到系统设置-应用管理中授权本程序获取相关权限后再重新运行本程序。");
                        AlertDialogUtil.showHint(this, sb.toString(), new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                    break;
                }
            }
            if(isGranted){
                goToTabActivity();
            }
        }


    }
}
