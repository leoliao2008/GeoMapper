package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapterr.OfflineMapAdminPagerAdapter;
import com.skycaster.geomapper.base.BaseActionBarActivity;

public class OffLineMapAdminActivity extends BaseActionBarActivity {
    private ViewPager mViewPager;
    private OfflineMapAdminPagerAdapter mPagerAdapter;
    private PagerTabStrip mTabStrip;

    public static void startActivity(Context context){
        context.startActivity(new Intent(context,OffLineMapAdminActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_off_line_map_admin;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.admin_off_line_map;
    }

    @Override
    protected void initChildViews() {
        mViewPager= (ViewPager) findViewById(R.id.activity_offline_map_vp);
        mTabStrip= (PagerTabStrip) findViewById(R.id.activity_offline_map_vp_tab_strip);
    }

    @Override
    protected void initRegularData() {
        mPagerAdapter =new OfflineMapAdminPagerAdapter(getSupportFragmentManager());
        mTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size_type_3));
        mTabStrip.setTextColor(Color.BLACK);
        mTabStrip.setTabIndicatorColorResource(R.color.colorSkyBlue);
        mTabStrip.setBackgroundColor(getResources().getColor(R.color.colorNaviBg));
        int padding= (int) getResources().getDimension(R.dimen.padding_size_type_1);
        mTabStrip.setPadding(padding,padding,padding,padding);
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void initListeners() {

    }




}
