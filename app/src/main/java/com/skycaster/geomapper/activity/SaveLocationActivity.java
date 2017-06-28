package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.customized.FullLengthListView;

/**
 * Created by 廖华凯 on 2017/6/27.
 */

public class SaveLocationActivity extends BaseActionBarActivity {
    private static String LATITUDE="latitude";
    private static String LONGITUDE="longitude";
    private static String ALTITUDE="altitude";
    private static String BAIDU_COORD="isBaiduCoordinateSystem";
    private static String LOCATION_INFO="locationInfo";
    private double latitude;
    private double longitude;
    private double altitude;
    private boolean isBaiduCoord;
    private String comments;
    private EditText edt_title;
    private EditText edt_catalogue;
    private Button btn_adminLocTags;
    private RadioGroup mRadioGroup;
    private TextView tv_latitude;
    private TextView tv_longitude;
    private TextView tv_altitude;
    private EditText edt_comments;
    private Button btn_save;
    private Button btn_photo;
    private Button btn_gallery;
    private FullLengthListView mListView;

    public static void start(Context context,double latitude,double longitude,double altitude,boolean isBaiduCoordSys,@Nullable String locInfo) {
        Intent starter = new Intent(context, SaveLocationActivity.class);
        starter.putExtra(LATITUDE,latitude);
        starter.putExtra(LONGITUDE,longitude);
        starter.putExtra(ALTITUDE,altitude);
        starter.putExtra(BAIDU_COORD,isBaiduCoordSys);
        starter.putExtra(LOCATION_INFO,locInfo);
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
        comments=intent.getStringExtra(LOCATION_INFO);
        if(TextUtils.isEmpty(comments)){
            comments="null";
        }

        tv_altitude.setText(getString(R.string.altitude)+altitude);
        tv_latitude.setText(getString(R.string.latitude)+latitude);
        tv_longitude.setText(getString(R.string.longitude)+longitude);
        edt_comments.setText(comments);
        edt_comments.setSelection(comments.length());
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_save_location;
    }

    @Override
    protected void initChildViews() {
        tv_altitude= (TextView) findViewById(R.id.activity_save_location_tv_altitude);
        tv_latitude= (TextView) findViewById(R.id.activity_save_location_tv_latitude);
        tv_longitude= (TextView) findViewById(R.id.activity_save_location_tv_longitude);
        edt_title= (EditText) findViewById(R.id.activity_save_location_edt_loc_title);
        edt_comments= (EditText) findViewById(R.id.activity_save_location_edt_comments);
        btn_adminLocTags = (Button) findViewById(R.id.activity_save_location_btn_admin_loc_tags);
        btn_save= (Button) findViewById(R.id.activity_save_location_btn_save);
        btn_photo= (Button) findViewById(R.id.activity_save_location_btn_photo);
        btn_gallery= (Button) findViewById(R.id.activity_save_location_btn_gallery);
        mListView= (FullLengthListView) findViewById(R.id.activity_save_location_lst_view);

    }

    @Override
    protected void initListeners() {
        btn_adminLocTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocTagAdminActivity.start(SaveLocationActivity.this);
            }
        });

    }
}
