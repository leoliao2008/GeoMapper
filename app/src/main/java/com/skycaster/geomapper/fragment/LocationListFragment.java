package com.skycaster.geomapper.fragment;

import android.os.Bundle;
import android.widget.ExpandableListView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.LocationListAdapter;
import com.skycaster.geomapper.base.BaseFragment;
import com.skycaster.geomapper.bean.LocRecordGroupItem;
import com.skycaster.geomapper.bean.Location;
import com.skycaster.geomapper.bean.LocationTag;
import com.skycaster.geomapper.data.LocTagListOpenHelper;
import com.skycaster.geomapper.data.LocationOpenHelper;
import com.skycaster.geomapper.interfaces.LocRecordEditCallBack;
import com.skycaster.geomapper.util.AlertDialogUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/30.
 */

public class LocationListFragment extends BaseFragment {
    private ExpandableListView mListView;
    private LocationListAdapter mAdapter;
    private ArrayList<LocRecordGroupItem> mGroupList =new ArrayList<>();
    private LocTagListOpenHelper mTagListOpenHelper;
    private LocationOpenHelper mLocationOpenHelper;
    private LocRecordEditCallBack mLocRecordEditCallBack;

    @Override
    protected int setContentView() {
        return R.layout.fragment_location_records;
    }

    @Override
    protected void initView() {
        mListView= (ExpandableListView) findViewById(R.id.fragment_location_record_exp_list_view);

    }

    @Override
    protected void initData(Bundle arguments) {
        mTagListOpenHelper = new LocTagListOpenHelper(getContext());
        mLocationOpenHelper = LocationOpenHelper.getInstance(getContext());
        mLocRecordEditCallBack=new LocRecordEditCallBack() {
            @Override
            public void onEdit(Location location,int groupPosition) {

            }

            @Override
            public void onDelete(final Location location, final int groupPosition) {
                AlertDialogUtil.showHint(getContext(), getString(R.string.warning_delete_loc_record), new Runnable() {
                    @Override
                    public void run() {
                        boolean result = mLocationOpenHelper.delete(location);
                        if(result){
                            showToast(getString(R.string.delete_success));
                            updateListView(groupPosition);
                        }else {
                            showToast(getString(R.string.delete_fails));
                        }
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        //do nothing
                    }
                });

            }
        };
        mAdapter=new LocationListAdapter(getContext(), mGroupList,mLocRecordEditCallBack);
        mListView.setAdapter(mAdapter);
        updateListView(0);
    }


    @Override
    protected void initListeners() {
        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int count = mListView.getChildCount();
                for(int i=0;i<count;i++){
                    if(i!=groupPosition&&mListView.isGroupExpanded(i)){
                        mListView.collapseGroup(i);
                    }
                }
            }
        });

    }

    private void updateListView(int groupPosition){
        mGroupList.clear();

        LocationTag defaultTag=new LocationTag(getString(R.string.default_tag_name),Integer.MAX_VALUE);
        LocRecordGroupItem untaggedGroup=new LocRecordGroupItem(defaultTag);
        mGroupList.add(untaggedGroup);


        ArrayList<LocationTag> tagList = mTagListOpenHelper.getTagList();
        for(LocationTag tag:tagList){
            mGroupList.add(new LocRecordGroupItem(tag));
        }

        ArrayList<Location> locations = mLocationOpenHelper.getLocationList();
        showLog("location size "+locations.size());
        for(Location location:locations){
            if(location!=null){
                showLog(location.toString());
                boolean isMatch=false;
                for(int i = 0, size = mGroupList.size(); i<size; i++){
                    LocationTag tag = location.getTag();
                    if(tag!=null&& mGroupList.get(i).getLocationTag().equals(tag)){
                        mGroupList.get(i).addLocation(location);
                        isMatch=true;
                        break;
                    }
                }
                if(!isMatch){
                    untaggedGroup.addLocation(location);
                }
            }
        }

        mAdapter.notifyDataSetChanged();
        if(mListView.isGroupExpanded(groupPosition)){
            mListView.collapseGroup(groupPosition);
            mListView.expandGroup(groupPosition,true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTagListOpenHelper.close();
        mLocationOpenHelper.close();
    }
}
