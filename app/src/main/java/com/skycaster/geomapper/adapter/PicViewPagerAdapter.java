package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.skycaster.geomapper.fragment.PicIteratorFragment;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/18.
 */

public class PicViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> mList;
    private Context mContext;

    public PicViewPagerAdapter(FragmentManager fm,ArrayList<String>list,Context context) {
        super(fm);
        mList=list;
        mContext=context;
    }


    @Override
    public Fragment getItem(int position) {
        return new PicIteratorFragment(mList.get(position));
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
