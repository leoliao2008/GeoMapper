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
import com.skycaster.geomapper.bean.AvailableOffLineMap;
import com.skycaster.geomapper.bean.LocalOffLineMap;
import com.skycaster.geomapper.fragment.AvailableOffLineMapsFragment;
import com.skycaster.geomapper.fragment.LocalMapListFragment;

import java.util.ArrayList;

public class OffLineMapAdminActivity extends BaseActionBarActivity {
    private ViewPager mViewPager;
    private OfflineMapAdminPagerAdapter mPagerAdapter;
    private PagerTabStrip mTabStrip;
    private MKOfflineMap mMkOfflineMap;
    private ArrayList<AvailableOffLineMap> mAvailableMapList =new ArrayList<>();
    private ArrayList<LocalOffLineMap> mLocalOffLineMapList = new ArrayList<>();
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

            }
        });

        //get local maps
        ArrayList<MKOLUpdateElement> localMaps = mMkOfflineMap.getAllUpdateInfo();
        if(localMaps!=null&&localMaps.size()>0){
            for(MKOLUpdateElement element:localMaps){
                mLocalOffLineMapList.add(new LocalOffLineMap(element));
            }
        }
        LocalMapListFragment mapListFragment=new LocalMapListFragment(this);
        mFragments.add(mapListFragment);

        //get available maps
        ArrayList<MKOLSearchRecord> availableMaps = mMkOfflineMap.getOfflineCityList();
        if(availableMaps!=null&&availableMaps.size()>0){
            for(MKOLSearchRecord city:availableMaps){
                iterateChildCities(city);
            }
        }
        for(LocalOffLineMap localOffLineMap:mLocalOffLineMapList){
            for(AvailableOffLineMap availableOffLineMap:mAvailableMapList){
                if(localOffLineMap.getCityId()==availableOffLineMap.getCityId()){
                    availableOffLineMap.setDownLoaded(true);
                    break;
                }
            }
        }
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

    private void iterateChildCities(MKOLSearchRecord city){
        ArrayList<MKOLSearchRecord> childCities = city.childCities;
        if(childCities!=null&&childCities.size()>0){
            for(MKOLSearchRecord c:childCities){
                iterateChildCities(c);
            }
        }else {
            mAvailableMapList.add(new AvailableOffLineMap(city));
        }
    }

    @Override
    protected void initListeners() {

    }

    public ArrayList<LocalOffLineMap> getLocalOffLineMapList(){
        return mLocalOffLineMapList;
    }

    public ArrayList<AvailableOffLineMap> getAvailableMapList(){
        return mAvailableMapList;
    }

    public MKOfflineMap getMkOfflineMap(){
        return mMkOfflineMap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMkOfflineMap.destroy();
    }
}
