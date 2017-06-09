package com.skycaster.geomapper.adapterr;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.fragment.AvailableOffLineMapsFragment;
import com.skycaster.geomapper.fragment.ExistingOffLineMapListFragment;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public class OfflineMapAdminPagerAdapter extends FragmentStatePagerAdapter {

    private String[] titles;
    private Context mContext;

    public OfflineMapAdminPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext=context;
        titles=new String[]{
                BaseApplication.getContext().getString(R.string.local_off_line_maps),
                BaseApplication.getContext().getString(R.string.down_load_off_line_maps)
        };
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new ExistingOffLineMapListFragment();
        }
        return new AvailableOffLineMapsFragment(mContext,null);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
