package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.MappingRecordPagerAdapter;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.fragment.LocationListFragment;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/30.
 */

public class GeoRecordsActivity extends BaseActionBarActivity {
    private ViewPager mViewPager;
    private PagerTabStrip mTabStrip;
    private ArrayList<Fragment> mFragments=new ArrayList<>();
    private MappingRecordPagerAdapter mPagerAdapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, GeoRecordsActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.location_records;
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_geo_records;
    }

    @Override
    protected void initChildViews() {
        mViewPager= (ViewPager) findViewById(R.id.activity_mapping_record_view_pager);
        mTabStrip= (PagerTabStrip) findViewById(R.id.activity_mapping_record_pager_tab_strip);

    }

    @Override
    protected void initRegularData() {

        LocationListFragment locationListFragment=new LocationListFragment();
        mFragments.add(locationListFragment);

        mTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size_type_3));
        mTabStrip.setTextColor(Color.BLACK);
        mTabStrip.setTabIndicatorColorResource(R.color.colorSkyBlue);
        mTabStrip.setBackgroundColor(getResources().getColor(R.color.colorNaviBg));
        int padding= (int) getResources().getDimension(R.dimen.padding_size_type_1);
        mTabStrip.setPadding(padding,padding,padding,padding);

        mPagerAdapter=new MappingRecordPagerAdapter(getSupportFragmentManager(),this,mFragments);
        mViewPager.setAdapter(mPagerAdapter);


    }

    @Override
    protected void initListeners() {

    }
}
