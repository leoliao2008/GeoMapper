package com.skycaster.geomapper.fragment;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseFragment;
import com.skycaster.geomapper.bean.MapDataGroupByTag;
import com.skycaster.geomapper.bean.MappingData;
import com.skycaster.geomapper.bean.Tag;
import com.skycaster.geomapper.data.MappingDataOpenHelper;
import com.skycaster.geomapper.data.MappingDataTagsOpenHelper;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/12.
 */

public class MappingDataAdminFragment extends BaseFragment {
    private LinearLayout ll_noData;
    private ExpandableListView mListView;
    private MappingDataOpenHelper mDataOpenHelper;
    private TextView tv_count;
    private ImageView iv_sort;
    private MappingDataTagsOpenHelper mTagsOpenHelper;
    private ArrayList<MapDataGroupByTag> groupListByTag=new ArrayList<>();

    @Override
    protected int setContentView() {
        return R.layout.fragment_mapping_data_admin;
    }

    @Override
    protected void initView() {
        ll_noData= (LinearLayout) findViewById(R.id.fragment_mapping_data_admin_ll_no_data);
        mListView= (ExpandableListView) findViewById(R.id.fragment_mapping_data_admin_exp_lst_view);

    }

    @Override
    protected void initData(Bundle arguments) {
        mDataOpenHelper =new MappingDataOpenHelper(getContext());
        ArrayList<MappingData> mappingDatas = mDataOpenHelper.getMappingDatas();
        mTagsOpenHelper = new MappingDataTagsOpenHelper(getContext());
        ArrayList<Tag> tagList = mTagsOpenHelper.getTagList();


    }

    @Override
    protected void initListeners() {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataOpenHelper.close();
        mTagsOpenHelper.close();

    }
}
