package com.skycaster.geomapper.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.customized.SatelliteMapView;
import com.skycaster.geomapper.util.AlertDialogUtil;

import java.util.ArrayList;

public class SatelliteMapActivity extends BaseActionBarActivity {
    private RelativeLayout mRootView;
    private LocationManager mLocationManager;
    private SatelliteMapView mSatelliteMapView;
    private ArrayList<GpsSatellite> mGpsSatellites=new ArrayList<>();
    private LocationListener mLocationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private GpsStatus.Listener mGpsStatusListener=new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            showLog("onGpsStatusChanged");
            GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
            switch (event){
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    int firstFix = gpsStatus.getTimeToFirstFix();
                    showLog("First Fix Time:"+firstFix);
                    int sec = firstFix / 1000;
                    int milli=firstFix-sec*1000;
                    tv_firstFixTime.setText(String.format("%02d",sec) +":"+String.format("%03d",milli));
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                    mGpsSatellites.clear();
                    for(GpsSatellite satellite:satellites){
                        mGpsSatellites.add(satellite);
                    }
                    int count = mSatelliteMapView.updateSatellites(mGpsSatellites);
                    tv_inView.setText(String.valueOf(count));
                    break;
                case GpsStatus.GPS_EVENT_STARTED:

                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    tv_firstFixTime.setText("GPS Off Line");
                    break;
                default:
                    break;
            }

        }
    };

    private SharedPreferences mSharedPreferences;
    private boolean isEnableCompassMode;
    private String ENABLE_COMPASS_MODE="enable_compass_mode";
    private TextView tv_inView;
    private TextView tv_firstFixTime;

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
        mRootView= (RelativeLayout) findViewById(R.id.activity_satellite_map_root_view);
        mSatelliteMapView= (SatelliteMapView) findViewById(R.id.activity_satellite_map_map_view);
        tv_inView= (TextView) findViewById(R.id.activity_satellite_map_tv_satellite_count);
        tv_firstFixTime= (TextView) findViewById(R.id.activity_satellite_map_tv_first_fix_time);

    }

    @Override
    protected void initData() {
        mSharedPreferences = getSharedPreferences("Config", MODE_PRIVATE);
        isEnableCompassMode=mSharedPreferences.getBoolean(ENABLE_COMPASS_MODE,false);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checkIfGpsOpen();
        tv_firstFixTime.setText("Initializing...");
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
        mLocationManager.addGpsStatusListener(mGpsStatusListener);
        mSatelliteMapView.enableCompassMode(isEnableCompassMode);

    }

    private void checkIfGpsOpen() {
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialogUtil.showHint(this, getString(R.string.advise_to_open_gps), new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(intent);
                    }catch (ActivityNotFoundException e1){
                        intent.setAction(Settings.ACTION_SETTINGS);
                        try {
                            startActivity(intent);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }, new Runnable() {
                @Override
                public void run() {
                    showToast(getString(R.string.malfunction_for_gps_not_available));
                }
            });
        }
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isFinishing()){
            mLocationManager.removeGpsStatusListener(mGpsStatusListener);
            mLocationManager.removeUpdates(mLocationListener);
            mSatelliteMapView.enableCompassMode(false);
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
}
