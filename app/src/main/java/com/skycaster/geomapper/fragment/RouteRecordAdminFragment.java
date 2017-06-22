package com.skycaster.geomapper.fragment;

import android.os.Bundle;
import android.widget.ListView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapterr.RouteAdminAdapter;
import com.skycaster.geomapper.base.BaseFragment;
import com.skycaster.geomapper.data.RouteIndexOpenHelper;
import com.skycaster.geomapper.interfaces.RouteRecordSelectedListener;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/22.
 */

public class RouteRecordAdminFragment extends BaseFragment {
    private ListView mListView;
    private ArrayList<String>mList=new ArrayList<>();
    private RouteAdminAdapter mAdapter;
    private RouteIndexOpenHelper mHelper;

    @Override
    protected int setContentView() {
        return R.layout.fragment_route_admin;
    }

    @Override
    protected void initView() {
        mListView= (ListView) findViewById(R.id.fragment_route_admin_lst_view);

    }

    @Override
    protected void initData(Bundle arguments) {
        mHelper = new RouteIndexOpenHelper(getContext());
        mList.addAll(mHelper.getRouteIndex());
        mAdapter=new RouteAdminAdapter(mList, getContext(),mHelper, new RouteRecordSelectedListener() {
            @Override
            public void onRouteRecordSelected(String recordName) {

            }

            @Override
            public void onRouteRecordEmpty() {

            }
        });
        mListView.setAdapter(mAdapter);

    }

    @Override
    protected void initListeners() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mHelper!=null){
            mHelper.close();
        }
    }
}
