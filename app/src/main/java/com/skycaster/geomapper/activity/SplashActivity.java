package com.skycaster.geomapper.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.geomapper.service.BeidouDataBroadcastingService;
import com.skycaster.geomapper.util.AlertDialogUtil;

public class SplashActivity extends BaseActivity {

    private static final int REQUEST_SYS_PERMISSIONS = 145;
    private String mSerialPortPath;
    private int mBaudRate;
    private SharedPreferences mSharedPreferences;
    private TextView tv_softwareInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initChildViews() {
        tv_softwareInfo= (TextView) findViewById(R.id.activity_splash_tv_soft_ware_info);

    }

    @Override
    protected void initBaseData() {
        //启动前台服务监听北斗串口
        mSharedPreferences=getSharedPreferences("Config",MODE_PRIVATE);
        mSerialPortPath =mSharedPreferences.getString(StaticData.SERIAL_PORT_PATH,"ttyS1");
        mBaudRate =mSharedPreferences.getInt(StaticData.SERIAL_PORT_BAUD_RATE,115200);
        Intent intent = new Intent(this, BeidouDataBroadcastingService.class);
        intent.putExtra(StaticData.SERIAL_PORT_PATH, mSerialPortPath);
        intent.putExtra(StaticData.SERIAL_PORT_BAUD_RATE,mBaudRate);
        startService(intent);

        //显示版本号
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            tv_softwareInfo.setText("@2017 深圳思凯微有限公司\nVer "+packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkPermissions()){
            goToTabActivity();
        }else {
            requestSysPermissions();
        }
    }

    private void requestSysPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(StaticData.SYS_PERMISSIONS,REQUEST_SYS_PERMISSIONS);
        }
    }

    private boolean checkPermissions() {
        boolean isGranted=true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for(String p: StaticData.SYS_PERMISSIONS){
                if(PackageManager.PERMISSION_GRANTED!=checkSelfPermission(p)){
                    isGranted=false;
                    break;
                }
            }
        }
        return isGranted;
    }

    private void goToTabActivity() {
        BaseApplication.postDelay(new Runnable() {
            @Override
            public void run() {
                NavigationActivity.startActivity(SplashActivity.this);
                BaseApplication.postDelay(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },2000);
            }
        },2000);
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
            for(String p: StaticData.SYS_PERMISSIONS){
                sb.append(p).append("\n");
            }
            for(int i=0;i<len;i++){
                if(PackageManager.PERMISSION_GRANTED!=grantResults[i]){
                    isGranted=false;
                    if(shouldShowRequestPermissionRationale(permissions[i])){
                        sb.append(getString(R.string.permission_choose_to_grant_or_quit));
                        AlertDialogUtil.showStandardDialog(this, sb.toString(), new Runnable() {
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
                        AlertDialogUtil.showStandardDialog(this, sb.toString(), new Runnable() {
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
