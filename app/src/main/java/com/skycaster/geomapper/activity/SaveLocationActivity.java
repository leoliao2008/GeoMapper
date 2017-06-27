package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;

/**
 * Created by 廖华凯 on 2017/6/27.
 */

public class SaveLocationActivity extends BaseActionBarActivity {
    private static String LATITUDE="latitude";
    private static String LONGITUDE="longitude";
    private static String ALTITUDE="altitude";
    private static String BAIDU_COORD="isBaiduCoordinateSystem";
    private double latitude;
    private double longitude;
    private double altitude;
    private boolean isBaiduCoord;
    private EditText edt_title;
    private EditText edt_catalogue;
    private Button btn_catalogueAdmin;
    private RadioGroup mRadioGroup;
    private TextView tv_latitude;
    private TextView tv_longitude;
    private TextView tv_altitude;
    private EditText edt_comments;
    private Button btn_save;
    private Button btn_photo;
    private Button btn_gallery;

    public static void start(Context context,double latitude,double longitude,double altitude,boolean isBaiduCoordSys) {
        Intent starter = new Intent(context, SaveLocationActivity.class);
        starter.putExtra(LATITUDE,latitude);
        starter.putExtra(LONGITUDE,longitude);
        starter.putExtra(ALTITUDE,altitude);
        starter.putExtra(BAIDU_COORD,isBaiduCoordSys);
        context.startActivity(starter);
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.save_location;
    }

    @Override
    protected void initRegularData() {
        Intent intent = getIntent();
        latitude=intent.getDoubleExtra(LATITUDE,0);
        longitude=intent.getDoubleExtra(LONGITUDE,0);
        altitude=intent.getDoubleExtra(ALTITUDE,0);
        isBaiduCoord=intent.getBooleanExtra(BAIDU_COORD,true);

    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_save_location;
    }

    @Override
    protected void initChildViews() {

    }

    @Override
    protected void initListeners() {

    }
}
