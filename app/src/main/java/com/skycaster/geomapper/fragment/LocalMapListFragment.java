package com.skycaster.geomapper.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;

import com.skycaster.geomapper.activity.OffLineMapAdminActivity;
import com.skycaster.geomapper.adapter.LocalMapListAdapter;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public class LocalMapListFragment extends ListFragment {

    private LocalMapListAdapter mAdapter;
    private OffLineMapAdminActivity mContext;


    public LocalMapListFragment(OffLineMapAdminActivity context) {
        mContext=context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter=new LocalMapListAdapter(mContext.getLocalOffLineMapList(),mContext);
        setListAdapter(mAdapter);
    }

    public void updateContents(){
        mAdapter.notifyDataSetChanged();
    }

}
