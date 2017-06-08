package com.skycaster.geomapper.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.skycaster.geomapper.adapterr.ExistingOffLineMapListAdapter;
import com.skycaster.geomapper.bean.ExistingOffLineMap;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public class ExistingOffLineMapListFragment extends ListFragment {

    private static final String CITY_LIST = "CITY_LIST";
    private ExistingOffLineMapListAdapter mAdapter;
    private ArrayList<ExistingOffLineMap> mList;

    public ExistingOffLineMapListFragment() {
//        SDKInitializer.initialize(BaseApplication.getContext());
        MKOfflineMap mkOfflineMap=new MKOfflineMap();
        mkOfflineMap.init(new MKOfflineMapListener() {
            @Override
            public void onGetOfflineMapState(int i, int i1) {

            }
        });
        ArrayList<MKOLUpdateElement> allUpdateInfo = mkOfflineMap.getAllUpdateInfo();
        ArrayList<ExistingOffLineMap> list=new ArrayList<>();
        if(allUpdateInfo!=null&&allUpdateInfo.size()>0){
            for(MKOLUpdateElement element:allUpdateInfo){
                list.add(new ExistingOffLineMap(element));
            }
        }
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(CITY_LIST,list);
        setArguments(arguments);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments!=null){
            mList = arguments.getParcelableArrayList(CITY_LIST);
        }else {
            mList=new ArrayList<>();
        }
        mAdapter=new ExistingOffLineMapListAdapter(mList,getContext());
        setListAdapter(mAdapter);
    }

}
