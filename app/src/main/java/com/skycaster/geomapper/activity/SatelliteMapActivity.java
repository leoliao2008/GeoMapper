package com.skycaster.geomapper.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.customized.SatelliteMapView;
import com.skycaster.geomapper.data.StaticData;
import com.skycaster.gps_decipher_lib.GPGSV.GPGSVBean;
import com.skycaster.gps_decipher_lib.GPSDataExtractor;
import com.skycaster.gps_decipher_lib.GPSDataExtractorCallBack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SatelliteMapActivity extends BaseActionBarActivity {
    private SatelliteMapView mSatelliteMapView;
    private SharedPreferences mSharedPreferences;
    private boolean isEnableCompassMode;
    private String ENABLE_COMPASS_MODE="enable_compass_mode";
    private TextView tv_satelliteCount;
    private TextView tv_firstFixTime;
    private GPSDataExtractorCallBack mGpsDataExtractorCallBack=new GPSDataExtractorCallBack() {
        @Override
        public void onGetGPGSVBean(final GPGSVBean bean) {
            super.onGetGPGSVBean(bean);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //更新卫星图
                    int updateCount = mSatelliteMapView.updateSatellites(bean);
                    //更新卫星个数
                    tv_satelliteCount.setText(String.valueOf(updateCount));
                    //如果是首次定位成功，显示首次定位花费的时间
                    if(mIsFirstFixTime.compareAndSet(true,false)){
                        if(mDisposable!=null&&!mDisposable.isDisposed()){
                            mDisposable.dispose();
                            mDisposable=null;
                        }
                        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss",Locale.CHINA);
                        Date date=new Date(mTimeElapsed);
                        tv_firstFixTime.setText(format.format(date));
                    }
                }
            });
        }
    };
    private AtomicBoolean mIsFirstFixTime=new AtomicBoolean(true);//是否首次定位
    private Disposable mDisposable;
    private long mTimeElapsed;
    private GPSDataReceiver mReceiver;
//    private GPIOModel mGPIOModel;


    public static void start(Context context) {
        Intent starter = new Intent(context, SatelliteMapActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_satellite_view;
    }


    @Override
    protected String setActionBarTitle() {
        return getResources().getString(R.string.function_satellite_map);
    }

    @Override
    protected void initChildViews() {
        mSatelliteMapView= (SatelliteMapView) findViewById(R.id.activity_satellite_map_map_view);
        tv_satelliteCount = (TextView) findViewById(R.id.activity_satellite_map_tv_satellite_count);
        tv_firstFixTime= (TextView) findViewById(R.id.activity_satellite_map_tv_first_fix_time);

    }

    @Override
    protected void initData() {
        mSharedPreferences = getSharedPreferences("Config", MODE_PRIVATE);
        isEnableCompassMode=mSharedPreferences.getBoolean(ENABLE_COMPASS_MODE,false);
        mSatelliteMapView.enableCompassMode(isEnableCompassMode);
        tv_firstFixTime.setText("Initializing...");
        //打开模块电源
//        mGPIOModel=new GPIOModel();
//        try {
//            mGPIOModel.turnOnAllModulesPow();
//        } catch (Exception e) {
//            ToastUtil.showToast(e.getMessage());
//        }
        //开始计算首次定位时间
        startCountingUntilFirstFix();
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //开始接收广播
        registerReceivers();
    }

    private void registerReceivers() {
        IntentFilter intentFilter=new IntentFilter(StaticData.ACTION_GPS_SERIAL_PORT_DATA);
        mReceiver = new GPSDataReceiver();
        registerReceiver(mReceiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止接收广播
        unregisterReceivers();
        if(isFinishing()){
            //释放资源
            mSatelliteMapView.enableCompassMode(false);

            //停止计时
            if(mDisposable!=null){
                if(!mDisposable.isDisposed()){
                    mDisposable.dispose();
                }
                mDisposable=null;
            }

            //关闭模块电源
//            try {
//                mGPIOModel.turnOffAllModulesPow();
//            } catch (IOException e) {
//                ToastUtil.showToast(e.getMessage());
//            }
        }
    }

    private void unregisterReceivers() {
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_satellite_map,menu);
        MenuItem item = menu.findItem(R.id.menu_satellite_map_toggle_compass_mode);
        if(isEnableCompassMode){
            item.setIcon(R.drawable.ic_compass_on);
        }else {
            item.setIcon(R.drawable.ic_compass_off);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_satellite_map_toggle_compass_mode){
            isEnableCompassMode=!isEnableCompassMode;
            mSharedPreferences.edit().putBoolean(ENABLE_COMPASS_MODE,isEnableCompassMode).apply();
            mSatelliteMapView.enableCompassMode(isEnableCompassMode);
            supportInvalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 开始计算卫星第一次成功定位前流逝的时间
     */
    private void startCountingUntilFirstFix(){
        Observable.interval(1, TimeUnit.MILLISECONDS, Schedulers.computation())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        mTimeElapsed=aLong;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private class GPSDataReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] bytes = intent.getByteArrayExtra(StaticData.EXTRA_BYTES_GPS_MODULE_SERIAL_PORT_DATA);
            GPSDataExtractor.decipherData(bytes,bytes.length, mGpsDataExtractorCallBack);

        }
    }
}
