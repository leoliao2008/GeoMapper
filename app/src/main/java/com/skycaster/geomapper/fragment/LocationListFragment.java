package com.skycaster.geomapper.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.AddLocationActivity;
import com.skycaster.geomapper.activity.EditLocationActivity;
import com.skycaster.geomapper.activity.LocationDetailActivity;
import com.skycaster.geomapper.adapter.LocationListAdapter;
import com.skycaster.geomapper.base.BaseFragment;
import com.skycaster.geomapper.bean.LocRecordGroupItem;
import com.skycaster.geomapper.bean.Location;
import com.skycaster.geomapper.bean.Tag;
import com.skycaster.geomapper.data.Constants;
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
    private ImageView iv_add;
    private TextView tv_count;
    private ImageView iv_clearAllData;

    @Override
    protected int setContentView() {
        return R.layout.fragment_location_records_admin;
    }

    @Override
    protected void initView() {
        mListView= (ExpandableListView) findViewById(R.id.fragment_location_record_exp_list_view);
        iv_add= (ImageView) findViewById(R.id.fragment_location_record_iv_add);
        tv_count= (TextView) findViewById(R.id.fragment_location_record_tv_count);
        iv_clearAllData= (ImageView) findViewById(R.id.fragment_location_record_iv_clear_all_data);

    }

    @Override
    protected void initData(Bundle arguments) {
        mTagListOpenHelper = new LocTagListOpenHelper(getContext());
        mLocationOpenHelper = LocationOpenHelper.getInstance(getContext());
        mLocRecordEditCallBack=new LocRecordEditCallBack() {
            @Override
            public void onEdit(Location location) {
                Intent intent=new Intent(getContext(), EditLocationActivity.class);
                intent.putExtra(Constants.LOCATION_INFO,location);
                startActivityForResult(intent,4213);


            }

            @Override
            public void onDelete(final Location location){
                AlertDialogUtil.showHint(getContext(), getString(R.string.warning_delete_loc_record), new Runnable() {
                    @Override
                    public void run() {
                        boolean result = mLocationOpenHelper.delete(location);
                        if(result){
                            showToast(getString(R.string.delete_success));
                            updateListView();
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

            @Override
            public void onViewDetail(Location location) {
                Intent intent=new Intent(getContext(),LocationDetailActivity.class);
                intent.putExtra(Constants.LOCATION_INFO,location);
                startActivityForResult(intent,1234);
            }
        };
        mAdapter=new LocationListAdapter(getContext(), mGroupList,mLocRecordEditCallBack);
        mListView.setAdapter(mAdapter);
        updateListView();
    }


    @Override
    protected void initListeners() {

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddLocationActivity.startForResult(LocationListFragment.this,1234,new Location());
            }
        });

        iv_clearAllData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogUtil.showHint(
                        getContext(),
                        getString(R.string.warning_delete_all_loc_record),
                        new Runnable() {
                            @Override
                            public void run() {
//                                boolean isSuccess=true;
//                                for(LocRecordGroupItem item:mGroupList){
//                                    ArrayList<Location> locations = item.getLocations();
//                                    for(Location location:locations){
//                                        isSuccess=mLocationOpenHelper.delete(location);
//                                        if(!isSuccess){
//                                            break;
//                                        }
//                                    }
//                                    if(!isSuccess){
//                                        break;
//                                    }
//                                }
                                boolean isSuccess=mLocationOpenHelper.deleteAll();
                                if(isSuccess){
                                    updateListView();
                                    int childCount = mAdapter.getGroupCount();
                                    for(int i=0;i<childCount;i++){
                                        mListView.expandGroup(i);
                                        mListView.collapseGroup(i);
                                    }
                                    showToast(getString(R.string.delete_success));
                                }else {
                                    showToast(getString(R.string.delete_fails));
                                }
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                //do nothing
                            }
                        }
                );

            }
        });


    }

    private void updateListView(){
        mGroupList.clear();

        Tag defaultTag=new Tag(getString(R.string.default_tag_name),Integer.MAX_VALUE);
        LocRecordGroupItem untaggedGroup=new LocRecordGroupItem(defaultTag);
        mGroupList.add(untaggedGroup);


        ArrayList<Tag> tagList = mTagListOpenHelper.getTagList();
        for(Tag tag:tagList){
            mGroupList.add(new LocRecordGroupItem(tag));
        }

        ArrayList<Location> locations = mLocationOpenHelper.getLocationList();
        tv_count.setText(String.valueOf(locations.size()));
        for(Location location:locations){
            if(location!=null){
//                showLog(location.toString());
                boolean isMatch=false;
                for(int i = 0, size = mGroupList.size(); i<size; i++){
                    Tag tag = location.getTag();
                    if(tag!=null&& mGroupList.get(i).getTag().equals(tag)){
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
        for(int i=0,count=mAdapter.getGroupCount();i<count;i++){
            if(mListView.isGroupExpanded(i)){
                mListView.collapseGroup(i);
                mListView.expandGroup(i);
            }else {
                mListView.expandGroup(i);
                mListView.collapseGroup(i);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTagListOpenHelper.close();
        mLocationOpenHelper.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case Constants.CONTENT_CHANGED:
                updateListView();
                break;
            default:
                break;
        }

    }
}
