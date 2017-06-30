package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public class MappingRecordPagerAdapter extends FragmentStatePagerAdapter {

    private String[] titles;
    private Context mContext;
    private ArrayList<Fragment> mList=new ArrayList<>();

    public MappingRecordPagerAdapter(FragmentManager fm, Context context, ArrayList<Fragment>list) {
        super(fm);
        mContext=context;
        titles=new String[]{
                BaseApplication.getContext().getString(R.string.history_location),
                BaseApplication.getContext().getString(R.string.history_routes)
        };
        mList=list;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }


}
