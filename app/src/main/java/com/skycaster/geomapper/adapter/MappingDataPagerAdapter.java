package com.skycaster.geomapper.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.activity.SaveMappingDataActivity;
import com.skycaster.geomapper.fragment.MappingDataBasicElementsFragment;
import com.skycaster.geomapper.fragment.MappingDataMapViewFragment;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/13.
 */

public class MappingDataPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment>mList=new ArrayList<>();
    private ArrayList<LatLng>mCoordinates;
    private SaveMappingDataActivity mActivity;
    public MappingDataPagerAdapter(FragmentManager fm, SaveMappingDataActivity activity,ArrayList<LatLng>coordinates) {
        super(fm);
        mActivity=activity;
        mCoordinates=coordinates;
        MappingDataBasicElementsFragment basicElementsFragment=new MappingDataBasicElementsFragment(activity,mCoordinates);
        MappingDataMapViewFragment mapViewFragment=new MappingDataMapViewFragment(activity,mCoordinates);
        mList.add(basicElementsFragment);
        mList.add(mapViewFragment);

    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
