package com.skycaster.geomapper.activity;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.customized.SatelliteMapView;

import java.util.ArrayList;

public class SatelliteMapActivity extends BaseActionBarActivity {
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
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                    Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                    mGpsSatellites.clear();
                    for(GpsSatellite satellite:satellites){
                        mGpsSatellites.add(satellite);
                    }
                    mSatelliteMapView.drawSatellites(mGpsSatellites);
                    break;
                case GpsStatus.GPS_EVENT_STARTED:
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    break;
                default:
                    break;
            }

        }
    };


    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_satellite_map;
    }


    @Override
    protected int getActionBarTitle() {
        return R.string.function_satellite_map;
    }

    @Override
    protected void initChildViews() {
        mSatelliteMapView= (SatelliteMapView) findViewById(R.id.activity_satellite_map_map_view);

    }

    @Override
    protected void initRegularData() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
        mLocationManager.addGpsStatusListener(mGpsStatusListener);
    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeGpsStatusListener(mGpsStatusListener);
        mLocationManager.removeUpdates(mLocationListener);
    }


}
