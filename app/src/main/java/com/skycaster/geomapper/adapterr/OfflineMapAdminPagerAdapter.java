package com.skycaster.geomapper.adapterr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.fragment.ExistingOffLineMapListFragment;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public class OfflineMapAdminPagerAdapter extends FragmentStatePagerAdapter {

    private String[] titles;

    public OfflineMapAdminPagerAdapter(FragmentManager fm) {
        super(fm);
        titles=new String[]{
                BaseApplication.getContext().getString(R.string.local_off_line_maps),
                BaseApplication.getContext().getString(R.string.down_load_off_line_maps)
        };
    }

    @Override
    public Fragment getItem(int position) {
        return new ExistingOffLineMapListFragment();
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
