package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.bean.MappingData;
import com.skycaster.geomapper.data.MappingDataOpenHelper;
import com.skycaster.geomapper.exceptions.EmptyInputException;
import com.skycaster.geomapper.exceptions.NullTagException;
import com.skycaster.geomapper.fragment.MappingDataBasicElementsFragment;
import com.skycaster.geomapper.fragment.MappingDataMapViewFragment;
import com.skycaster.geomapper.fragment.MappingDataPicsFragment;
import com.skycaster.geomapper.util.ToastUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/13.
 */

public class MappingDataPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment>mList=new ArrayList<>();
    private ArrayList<LatLng>mCoordinates;
    private Context mContext;
    private final MappingDataBasicElementsFragment mBasicElementsFragment;
    private final MappingDataMapViewFragment mMapViewFragment;
    private final MappingDataPicsFragment mPicListFragment;

    public MappingDataPagerAdapter(FragmentManager fm, Context context,ArrayList<LatLng>coordinates) {
        super(fm);
        mContext=context;
        mCoordinates=coordinates;
        mBasicElementsFragment = new MappingDataBasicElementsFragment(context,mCoordinates);
        mMapViewFragment = new MappingDataMapViewFragment(context,mCoordinates);
        mPicListFragment = new MappingDataPicsFragment();
        mList.add(mBasicElementsFragment);
        mList.add(mMapViewFragment);
        mList.add(mPicListFragment);

    }

    public boolean saveData(){
        boolean isSuccess;
        MappingData data=new MappingData();
        try {
            data=mBasicElementsFragment.updateBasicData(data);
        } catch (EmptyInputException e) {
            showToast(e.getMessage());
            return false;
        } catch (NullTagException e) {
            showToast(e.getMessage());
            return false;
        }
        data=mMapViewFragment.updateMappingData(data);
        data=mPicListFragment.updateMappingData(data);
        MappingDataOpenHelper helper=new MappingDataOpenHelper(mContext);
        isSuccess=helper.add(data);
        helper.close();
        return isSuccess;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    private void showToast(String msg){
        ToastUtil.showToast(msg);
    }
}
