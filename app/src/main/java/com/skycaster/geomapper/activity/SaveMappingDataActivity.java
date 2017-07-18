package com.skycaster.geomapper.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.MappingDataPagerAdapter;
import com.skycaster.geomapper.base.BaseActionBarActivity;

import java.util.ArrayList;

import static com.skycaster.geomapper.R.layout.activity_save_mapping_data;

public class SaveMappingDataActivity extends BaseActionBarActivity {

    private BottomNavigationView mNavigation;
    private ViewPager mViewPager;
    private MappingDataPagerAdapter mPagerAdapter;
    private static final String EXTRA_COORDINATES="coordinates";
    private ArrayList<LatLng> mCoordinates;
    private boolean mIsSuccess;


    public static void start(Context context, ArrayList<LatLng>coordinates) {
        Intent intent = new Intent(context, SaveMappingDataActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_COORDINATES,coordinates);
        context.startActivity(intent);
    }

    public static void startForResult(Activity activity,int requestCode,ArrayList<LatLng>coordinates){
        Intent intent = new Intent(activity, SaveMappingDataActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_COORDINATES,coordinates);
        activity.startActivityForResult(intent,requestCode);
    }


    @Override
    protected int setRootViewLayout() {
        return activity_save_mapping_data;
    }

    @Override
    protected void initChildViews() {
        mViewPager= (ViewPager) findViewById(R.id.activity_save_mapping_data_view_pager);

        mNavigation = (BottomNavigationView) findViewById(R.id.activity_save_mapping_data_navigation_view);

    }

    @Override
    protected void initRegularData() {
        Intent intent = getIntent();
        if(intent!=null){
            mCoordinates = intent.getParcelableArrayListExtra(EXTRA_COORDINATES);
        }
        mPagerAdapter=new MappingDataPagerAdapter(getSupportFragmentManager(),this,mCoordinates);
        mViewPager.setAdapter(mPagerAdapter);

    }

    @Override
    protected int getActionBarTitle() {
        return R.string.save_mapping_data;
    }

    @Override
    protected void initListeners() {

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MenuItem item = mNavigation.getMenu().getItem(position);
                item.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_mapping_data_basic_data:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.menu_mapping_data_map:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.menu_mapping_data_basic_pic:
                        mViewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_mapping_data,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_save_mapping_data_save:
                mIsSuccess = mPagerAdapter.saveData();
                if(mIsSuccess){
                    showToast(getString(R.string.save_success));
                    onBackPressed();
                }else {
                    showToast(getString(R.string.save_fails));
                }
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mIsSuccess){
            setResult(RESULT_OK);
        }else {
            setResult(RESULT_CANCELED);
        }
        super.onBackPressed();
    }
}
