package com.skycaster.geomapper.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.Window;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActivity;
import com.skycaster.geomapper.data.Constants;
import com.skycaster.geomapper.service.PortDataBroadcastingService;
import com.skycaster.geomapper.util.AlertDialogUtil;

import java.io.File;
import java.io.IOException;

import project.SerialPort.SerialPort;

public class SplashActivity extends BaseActivity {

    private static final int REQUEST_SYS_PERMISSIONS = 145;
    private String serialPortPath;
    private int baudRate;
    private SharedPreferences mSharedPreferences;
    private SerialPort mSerialPort;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
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
        mSharedPreferences=getSharedPreferences("Config",MODE_PRIVATE);
        serialPortPath=mSharedPreferences.getString(Constants.SERIAL_PORT_PATH,"ttyAMA04");
        baudRate=mSharedPreferences.getInt(Constants.SERIAL_PORT_BAUD_RATE,19200);
        try {
            mSerialPort = new SerialPort(new File(serialPortPath),baudRate,0);
        } catch (SecurityException e){
            e.printStackTrace();
        } catch (IOException paramE) {
            paramE.printStackTrace();
        }
        if(mSerialPort!=null){
            PortDataBroadcastingService.setSerialPort(mSerialPort);
            startService(new Intent(this, PortDataBroadcastingService.class));
        }
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
            sb.append(getString(R.string.explain_permissions)).append("\n");
            for(String p:Constants.SYS_PERMISSIONS){
                sb.append(p).append("\n");
            }
            for(int i=0;i<len;i++){
                if(PackageManager.PERMISSION_GRANTED!=grantResults[i]){
                    isGranted=false;
                    if(shouldShowRequestPermissionRationale(permissions[i])){
                        sb.append(getString(R.string.permission_choose_to_grant_or_quit));
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
                        sb.append(getString(R.string.quit_to_set_permissions));
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
