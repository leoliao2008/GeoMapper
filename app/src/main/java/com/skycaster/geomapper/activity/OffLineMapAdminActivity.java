package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapterr.OfflineMapAdminPagerAdapter;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.fragment.AvailableOffLineMapsFragment;
import com.skycaster.geomapper.fragment.LocalMapListFragment;

import java.util.ArrayList;

public class OffLineMapAdminActivity extends BaseActionBarActivity {
    private ViewPager mViewPager;
    private OfflineMapAdminPagerAdapter mPagerAdapter;
    private PagerTabStrip mTabStrip;
    private MKOfflineMap mMkOfflineMap;
    private ArrayList<MKOLSearchRecord> mAvailableMapList =new ArrayList<>();
    private ArrayList<MKOLUpdateElement> mLocalOffLineMapList = new ArrayList<>();
    private ArrayList<Fragment> mFragments=new ArrayList<>();


    public static void startActivity(Context context){
        context.startActivity(new Intent(context,OffLineMapAdminActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_off_line_map_admin;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.admin_off_line_map;
    }

    @Override
    protected void initChildViews() {
        mViewPager= (ViewPager) findViewById(R.id.activity_offline_map_vp);
        mTabStrip= (PagerTabStrip) findViewById(R.id.activity_offline_map_vp_tab_strip);
    }

    @Override
    protected void initRegularData() {
        //init off line map manager
        mMkOfflineMap = new MKOfflineMap();
        mMkOfflineMap.init(new MKOfflineMapListener() {
            @Override
            public void onGetOfflineMapState(int i, int i1) {
                switch (i){
                    case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
                        MKOLUpdateElement info = mMkOfflineMap.getUpdateInfo(i1);
                        getLocalMaps();
                        mPagerAdapter.updateDownLoadingView();

//                        Intent intent=new Intent(MapDownLoadProgressReceiver.ACTION);
//                        intent.putExtra(MapDownLoadProgressReceiver.CITY_NAME,info.cityName);
//                        intent.putExtra(MapDownLoadProgressReceiver.CITY_ID,info.cityID);
//                        intent.putExtra(MapDownLoadProgressReceiver.RATIO,info.ratio);
//                        intent.putExtra(MapDownLoadProgressReceiver.STATUS,info.status);
//                        sendBroadcast(intent);
//                        updateDownLoadView();
                        if(info.ratio==100){
                            //下载完成
                            updateAvailableView();
                        }
                        break;
                    case MKOfflineMap.TYPE_NEW_OFFLINE:
                        break;
                    case MKOfflineMap.TYPE_VER_UPDATE:
                        break;
                    case MKOfflineMap.TYPE_NETWORK_ERROR:
                        break;
                }

            }
        });

        //get local maps
        getLocalMaps();
        LocalMapListFragment mapListFragment=new LocalMapListFragment(this);
        mFragments.add(mapListFragment);

        //get available maps
        getAllAvailableMapList();
        AvailableOffLineMapsFragment availableOffLineMapsFragment=new AvailableOffLineMapsFragment(this);
        mFragments.add(availableOffLineMapsFragment);

        //put maps into a view pager
        mPagerAdapter =new OfflineMapAdminPagerAdapter(getSupportFragmentManager(),this,mFragments);
        mTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size_type_3));
        mTabStrip.setTextColor(Color.BLACK);
        mTabStrip.setTabIndicatorColorResource(R.color.colorSkyBlue);
        mTabStrip.setBackgroundColor(getResources().getColor(R.color.colorNaviBg));
        int padding= (int) getResources().getDimension(R.dimen.padding_size_type_1);
        mTabStrip.setPadding(padding,padding,padding,padding);
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void updateAvailableView() {
        getAllAvailableMapList();
        mPagerAdapter.updateAvailableView();
    }

    private void updateDownLoadView() {
        getLocalMaps();
        mPagerAdapter.updateDownLoadingView();
    }


    private void updateAllViews() {
        getLocalMaps();
        getAllAvailableMapList();
        mPagerAdapter.updateAllViews();
    }

    private void getLocalMaps(){
        mLocalOffLineMapList.clear();
        ArrayList<MKOLUpdateElement> elements = mMkOfflineMap.getAllUpdateInfo();
        if(elements!=null&&elements.size()>0){
            mLocalOffLineMapList.addAll(elements);
        }
    }

    private void getAllAvailableMapList(){
        mAvailableMapList.clear();
        ArrayList<MKOLSearchRecord> offlineCityList = mMkOfflineMap.getOfflineCityList();
        if(offlineCityList!=null&&offlineCityList.size()>0){
            for(MKOLSearchRecord city:offlineCityList){
                iterateChildCities(city);
            }
        }
    }

    private void iterateChildCities(MKOLSearchRecord city){
        ArrayList<MKOLSearchRecord> childCities = city.childCities;
        if(childCities!=null&&childCities.size()>0){
            for(MKOLSearchRecord c:childCities){
                iterateChildCities(c);
            }
        }else {
            mAvailableMapList.add(city);
        }
    }

    @Override
    protected void initListeners() {

    }

    public ArrayList<MKOLUpdateElement> getLocalOffLineMapList(){
        return mLocalOffLineMapList;
    }

    public ArrayList<MKOLSearchRecord> getAvailableMapList(){
        return mAvailableMapList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMkOfflineMap.destroy();
    }

    public boolean startDownLoad(int cityID) {
        boolean result = mMkOfflineMap.start(cityID);
        updateDownLoadView();
        return result;
    }

    public boolean pauseDownLoad(int cityID){
        return mMkOfflineMap.pause(cityID);
    }

    public boolean updateCity(int cityID){
        return mMkOfflineMap.update(cityID);
    }

    public boolean removeCity(int cityID){
        boolean result = mMkOfflineMap.remove(cityID);
        updateAllViews();
        return result;
    }

    public void searchCity(String cityName){
        mAvailableMapList.clear();
        ArrayList<MKOLSearchRecord> records = mMkOfflineMap.searchCity(cityName);
        if(records!=null){
            mAvailableMapList.addAll(records);
            mPagerAdapter.updateAllViews();
        }
    }

    public int getCityStatus(int cityID){
        MKOLUpdateElement element = mMkOfflineMap.getUpdateInfo(cityID);
        if(element!=null){
            return element.status;
        }
        return 0;
    }

    public MKOLUpdateElement getCityUpdateElement(int cityID){
        return mMkOfflineMap.getUpdateInfo(cityID);
    }


}
