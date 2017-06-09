package com.skycaster.geomapper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapterr.AvailableOffLineMapListAdapter;
import com.skycaster.geomapper.base.BaseFragment;
import com.skycaster.geomapper.bean.AvailableOffLineMap;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public class AvailableOffLineMapsFragment extends BaseFragment {
    private ListView mListView;
    private MKOfflineMap mMkOfflineMap;
    private ArrayList<AvailableOffLineMap> mList=new ArrayList<>();
    private AvailableOffLineMapListAdapter mAdapter;

    public AvailableOffLineMapsFragment() {

    }

    public AvailableOffLineMapsFragment(Context context, Bundle bundle) {
        super(context, bundle);
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
        mMkOfflineMap = new MKOfflineMap();
        mMkOfflineMap.init(new MKOfflineMapListener() {
            @Override
            public void onGetOfflineMapState(int i, int i1) {

            }
        });
        ArrayList<MKOLSearchRecord> cityList = mMkOfflineMap.getOfflineCityList();
        if(cityList!=null&&cityList.size()>0){
            for(MKOLSearchRecord city:cityList){
                iterateChildCities(city);
            }
        }
        mAdapter=new AvailableOffLineMapListAdapter(mList,mContext);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void initListeners() {


    }

    private void iterateChildCities(MKOLSearchRecord city){
        ArrayList<MKOLSearchRecord> childCities = city.childCities;
        if(childCities!=null&&childCities.size()>0){
            for(MKOLSearchRecord c:childCities){
                iterateChildCities(c);
            }
        }else {
            mList.add(new AvailableOffLineMap(city));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMkOfflineMap.destroy();
    }
}
