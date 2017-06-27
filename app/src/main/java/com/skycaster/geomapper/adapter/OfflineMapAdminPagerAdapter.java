package com.skycaster.geomapper.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.OffLineMapAdminActivity;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.fragment.AvailableOffLineMapsFragment;
import com.skycaster.geomapper.fragment.LocalMapListFragment;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public class OfflineMapAdminPagerAdapter extends FragmentStatePagerAdapter {

    private String[] titles;
    private OffLineMapAdminActivity mContext;
    private ArrayList<Fragment> mList=new ArrayList<>();

    public OfflineMapAdminPagerAdapter(FragmentManager fm, OffLineMapAdminActivity context,ArrayList<Fragment>list) {
        super(fm);
        mContext=context;
        titles=new String[]{
                BaseApplication.getContext().getString(R.string.local_off_line_maps),
                BaseApplication.getContext().getString(R.string.down_load_off_line_maps)
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

    public void updateAllViews(){
        updateDownLoadingView();
        updateAvailableView();
    }

    public void updateDownLoadingView(){
        LocalMapListFragment f0 = (LocalMapListFragment) mList.get(0);
        f0.updateContents();
    }

    public void updateAvailableView(){
        AvailableOffLineMapsFragment f1 = (AvailableOffLineMapsFragment) mList.get(1);
        f1.updateContents();
    }
}
