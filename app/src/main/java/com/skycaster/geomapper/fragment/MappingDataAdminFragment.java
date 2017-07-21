package com.skycaster.geomapper.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.MappingDetailActivity;
import com.skycaster.geomapper.activity.SaveMappingDataActivity;
import com.skycaster.geomapper.adapter.MappingDataByDateExpListAdapter;
import com.skycaster.geomapper.adapter.MappingDataByTagExpListAdapter;
import com.skycaster.geomapper.base.BaseFragment;
import com.skycaster.geomapper.bean.MapDataGroupByDate;
import com.skycaster.geomapper.bean.MapDataGroupByTag;
import com.skycaster.geomapper.bean.MappingData;
import com.skycaster.geomapper.bean.Tag;
import com.skycaster.geomapper.data.Constants;
import com.skycaster.geomapper.data.MappingDataOpenHelper;
import com.skycaster.geomapper.data.MappingDataTagsOpenHelper;
import com.skycaster.geomapper.interfaces.MappingDataEditCallBack;
import com.skycaster.geomapper.util.AlertDialogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/7/12.
 */

public class MappingDataAdminFragment extends BaseFragment {
    private ExpandableListView mExpListView;
    private MappingDataOpenHelper mDataOpenHelper;
    private TextView tv_count;
    private ImageView iv_sort;
    private MappingDataTagsOpenHelper mTagsOpenHelper;
    private ArrayList<MapDataGroupByTag> groupListByTag=new ArrayList<>();
    private ArrayList<MapDataGroupByDate> groupListByDate=new ArrayList<>();
    private MappingDataByTagExpListAdapter mAdapterByTag;
    private MappingDataByDateExpListAdapter mAdapterByDate;
    private PopupWindow mWindow;
    private int pattern;
    private static final int PATTERN_BY_TYPE=0;
    private static final int PATTERN_BY_DATE=1;
    private SimpleDateFormat mDateFormat=new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
    private SharedPreferences mSharedPreferences;
    private MappingDataEditCallBack mCallBack;
    private int REQUEST_EDIT_DATA=3691;


    @Override
    protected int setContentView() {
        return R.layout.fragment_mapping_data_admin;
    }

    @Override
    protected void initView() {
        mExpListView = (ExpandableListView) findViewById(R.id.fragment_mapping_data_admin_exp_lst_view);
        tv_count= (TextView) findViewById(R.id.fragment_mapping_data_admin_tv_data_count);
        iv_sort= (ImageView) findViewById(R.id.fragment_mapping_data_admin_iv_sort);
    }

    @Override
    protected void initData(Bundle arguments) {
        mDataOpenHelper =new MappingDataOpenHelper(getContext());
        mTagsOpenHelper = new MappingDataTagsOpenHelper(getContext());
        mSharedPreferences=getContext().getSharedPreferences("Config", Context.MODE_PRIVATE);
        pattern=mSharedPreferences.getInt(Constants.LAYOUT_PATTERN,0);
        mCallBack = new MappingDataEditCallBack() {
            @Override
            public void onDelete(final MappingData data, final int groupPosition, int childPosition) {
                AlertDialogUtil.showHint(
                        getContext(),
                        getString(R.string.warning_delete_loc_record),
                        new Runnable() {
                            @Override
                            public void run() {
                                boolean result = mDataOpenHelper.delete(data);
                                if(result){
                                    showToast(getString(R.string.delete_success));
                                    int newCount = Integer.valueOf(tv_count.getText().toString()) - 1;
                                    tv_count.setText(String.valueOf(newCount));
                                    switch (pattern){
                                        case PATTERN_BY_DATE:
                                            mAdapterByTag=null;
                                            groupListByDate.get(groupPosition).removeData(data);
                                            mAdapterByDate.notifyDataSetChanged();
                                            break;
                                        case PATTERN_BY_TYPE:
                                            mAdapterByDate=null;
                                            groupListByTag.get(groupPosition).removeData(data);
                                            mAdapterByTag.notifyDataSetChanged();
                                            break;
                                    }

                                }else {
                                    showToast(getString(R.string.delete_fails));
                                }
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                showToast(getString(R.string.hint_you_have_cancled_the_operation));
                            }
                        }
                );
            }

            @Override
            public void onEdit(MappingData data,int groupPosition,int childPosition) {
                SaveMappingDataActivity.startForResult(MappingDataAdminFragment.this,data,groupPosition,childPosition,REQUEST_EDIT_DATA);
            }

            @Override
            public void onViewDetails(MappingData data,int groupPosition,int childPosition) {
                MappingDetailActivity.startForResult(MappingDataAdminFragment.this,data,groupPosition,childPosition,REQUEST_EDIT_DATA);

            }
        };
        toggleExpListView(pattern);

    }


    private void toggleExpListView(int pattern) {
        switch (pattern){
            case PATTERN_BY_TYPE:
                updateGroupsByType();
                break;
            case PATTERN_BY_DATE:
                updateGroupByDate();
                break;
        }

    }

    private void updateGroupByDate() {
        if(mAdapterByDate==null){
            groupListByDate.clear();
            ArrayList<MappingData> mappingDatas = mDataOpenHelper.getMappingDatas();
            tv_count.setText(String.valueOf(mappingDatas.size()));
            ArrayList<String> dates=new ArrayList<>();
            for (MappingData data:mappingDatas){
                String date = mDateFormat.format(new Date(data.getId()));
                if(!dates.contains(date)){
                    dates.add(date);
                }
            }
            for(String date:dates){
                MapDataGroupByDate item=new MapDataGroupByDate(date);
                groupListByDate.add(item);
            }
            for(MappingData data:mappingDatas){
                String date = mDateFormat.format(new Date(data.getId()));
                for(MapDataGroupByDate item:groupListByDate){
                    if(item.getDate().equals(date)){
                        item.addData(data);
                        break;
                    }
                }
            }
            mAdapterByDate=new MappingDataByDateExpListAdapter(getContext(), groupListByDate, mCallBack);
        }
        mExpListView.setAdapter(mAdapterByDate);
        mAdapterByDate.notifyDataSetChanged();

    }

    private void updateGroupsByType() {
        if(mAdapterByTag ==null){
            groupListByTag.clear();
            ArrayList<MappingData> mappingDatas = mDataOpenHelper.getMappingDatas();
            tv_count.setText(String.valueOf(mappingDatas.size()));
            ArrayList<Tag> tagList = mTagsOpenHelper.getTagList();
            Tag defaultTag=new Tag(getString(R.string.default_tag_name),-1);
            tagList.add(0,defaultTag);
            for (Tag tag:tagList){
                MapDataGroupByTag item=new MapDataGroupByTag(tag);
                groupListByTag.add(item);
            }
            boolean isMatch;
            for(MappingData data:mappingDatas){
                isMatch=false;
                int id = data.getTagID();
                for(int i=0;i<tagList.size();i++){
                    Tag tag = tagList.get(i);
                    if(tag.getId()==id){
                        isMatch=true;
                        groupListByTag.get(i).addData(data);
                        break;
                    }
                }
                if(!isMatch){
                    groupListByTag.get(0).addData(data);
                }
            }
            tv_count.setText(String.valueOf(mappingDatas.size()));
            mAdapterByTag =new MappingDataByTagExpListAdapter(getContext(),groupListByTag,mCallBack);
        }
        mExpListView.setAdapter(mAdapterByTag);
    }



    private void resetExpListView(){
        mAdapterByDate=null;
        mAdapterByTag=null;
        toggleExpListView(pattern);
    }

    @Override
    protected void initListeners() {
        iv_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindowShiftTaggingPattern();
            }
        });
        mExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                MappingData data=null;
                switch (pattern){
                    case PATTERN_BY_DATE:
                        data=groupListByDate.get(groupPosition).getList().get(childPosition);
                        break;
                    case PATTERN_BY_TYPE:
                        data=groupListByTag.get(groupPosition).getList().get(childPosition);
                        break;
                }
                if(data!=null){
                    MappingDetailActivity.startForResult(MappingDataAdminFragment.this,data,groupPosition,childPosition,REQUEST_EDIT_DATA);
                }
                return true;
            }
        });

    }

    private void popWindowShiftTaggingPattern() {
        RadioGroup rgp= (RadioGroup) View.inflate(getContext(),R.layout.view_pop_win_mapping_data_layout_pattern,null);
        switch (pattern){
            case PATTERN_BY_DATE:
                rgp.check(R.id.pop_win_layout_pattern_rbtn_by_date);
                break;
            case PATTERN_BY_TYPE:
                rgp.check(R.id.pop_win_layout_pattern_rbtn_by_type);
                break;
        }
        rgp.measure(0,0);
        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.pop_win_layout_pattern_rbtn_by_type:
                        pattern=PATTERN_BY_TYPE;
                        toggleExpListView(PATTERN_BY_TYPE);
                        break;
                    case R.id.pop_win_layout_pattern_rbtn_by_date:
                        pattern=PATTERN_BY_DATE;
                        toggleExpListView(PATTERN_BY_DATE);
                        break;
                }
                mSharedPreferences.edit().putInt(Constants.LAYOUT_PATTERN, pattern).apply();
                mWindow.dismiss();
            }
        });
        mWindow = new PopupWindow(rgp);
        mWindow.setOutsideTouchable(true);
        mWindow.setFocusable(true);
        mWindow.setWidth(rgp.getMeasuredWidth());
        mWindow.setHeight(rgp.getMeasuredHeight());
        mWindow.showAsDropDown(iv_sort);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataOpenHelper.close();
        mTagsOpenHelper.close();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_EDIT_DATA&&data!=null){
            int groupPosition = data.getIntExtra(Constants.GROUP_POSITION, 0);
            int childPosition = data.getIntExtra(Constants.CHILD_POSITION,0);
            boolean isTagChanged=data.getBooleanExtra(Constants.IS_TAG_MODIFIED,false);
            MappingData newMappingData=data.getParcelableExtra(Constants.MAPPING_DATA_SAVED);
            switch (resultCode){
                case Activity.RESULT_OK:
                    switch (pattern){
                        case PATTERN_BY_DATE:
                            mAdapterByTag=null;
                            MapDataGroupByDate groupOfDate = groupListByDate.get(groupPosition);
                            groupOfDate.removeData(childPosition);
                            groupOfDate.addData(childPosition,newMappingData);
                            mAdapterByDate.notifyDataSetChanged();
                            break;
                        case PATTERN_BY_TYPE:
                            mAdapterByDate=null;
                            MapDataGroupByTag groupOfType = groupListByTag.get(groupPosition);
                            groupOfType.removeData(childPosition);
                            int tagID = newMappingData.getTagID();
                            if(!isTagChanged){
                                if(groupOfType.getTagId()== tagID){
                                    groupOfType.addData(childPosition,newMappingData);
                                }else {
                                    for(MapDataGroupByTag temp:groupListByTag){
                                        if(temp.getTagId()==tagID){
                                            temp.addData(newMappingData);
                                            break;
                                        }
                                    }
                                }
                                mAdapterByTag.notifyDataSetChanged();
                            }else {
                                resetExpListView();
                            }

                            break;
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    showToast(getString(R.string.hint_you_have_cancled_the_operation));
                    if(isTagChanged){
                        resetExpListView();
                    }
                    break;
            }
        }
    }
}
