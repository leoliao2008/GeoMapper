package com.skycaster.geomapper.fragment;

import android.os.Bundle;
import android.widget.ListView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.OffLineMapAdminActivity;
import com.skycaster.geomapper.adapterr.AvailableOffLineMapListAdapter;
import com.skycaster.geomapper.base.BaseFragment;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public class AvailableOffLineMapsFragment extends BaseFragment {
    private ListView mListView;
    private OffLineMapAdminActivity mContext;
    private AvailableOffLineMapListAdapter mAdapter;

    public AvailableOffLineMapsFragment(OffLineMapAdminActivity context) {
        mContext=context;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_available_ol_map;
    }

    @Override
    protected void initView() {
        mListView= (ListView) findViewById(R.id.fragment_available_ol_map_lst_view_all);
    }

    @Override
    protected void initData(Bundle arguments) {
        mAdapter=new AvailableOffLineMapListAdapter(mContext.getAvailableMapList(),mContext);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void initListeners() {

    }

    public void updateContents(){
        mAdapter.notifyDataSetChanged();
    }

}
