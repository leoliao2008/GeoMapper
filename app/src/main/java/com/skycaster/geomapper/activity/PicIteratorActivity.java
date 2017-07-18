package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.PicViewPagerAdapter;
import com.skycaster.geomapper.base.BaseActionBarActivity;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/18.
 */

public class PicIteratorActivity extends BaseActionBarActivity {
    private ViewPager mViewPager;
    private PicViewPagerAdapter mAdapter;
    private static final String PIC_PATHS="pic_paths";
    private static final String POSITION="position";
    private ArrayList<String> mPaths;
    private int mPosition;

    public static void start(Context context,ArrayList<String> paths,int turnToPosition) {
        Intent starter = new Intent(context, PicIteratorActivity.class);
        starter.putStringArrayListExtra(PIC_PATHS,paths);
        starter.putExtra(POSITION,turnToPosition);
        context.startActivity(starter);
    }
    @Override
    protected int getActionBarTitle() {
        return R.string.pic_detail;
    }



    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_pic;
    }

    @Override
    protected void initChildViews() {
        mViewPager= (ViewPager) findViewById(R.id.activity_pic_view_pager);
    }

    @Override
    protected void initRegularData() {
        Intent intent = getIntent();
        mPaths=intent.getStringArrayListExtra(PIC_PATHS);
        mPosition = intent.getIntExtra(POSITION, 0);
        mAdapter=new PicViewPagerAdapter(getSupportFragmentManager(),mPaths,this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mPosition);
    }

    @Override
    protected void initListeners() {

    }
}
