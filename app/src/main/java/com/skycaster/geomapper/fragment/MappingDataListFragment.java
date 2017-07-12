package com.skycaster.geomapper.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.adapter.MappingDataListAdapter;
import com.skycaster.geomapper.base.BaseFragment;
import com.skycaster.geomapper.bean.MappingData;
import com.skycaster.geomapper.data.MappingDataOpenHelper;
import com.skycaster.geomapper.interfaces.MappingDataEditCallBack;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/12.
 */

public class MappingDataListFragment extends BaseFragment {
    private LinearLayout ll_noData;
    private ListView mListView;
    private MappingDataListAdapter mAdapter;
    private ArrayList<MappingData> mList=new ArrayList<>();
    private MappingDataOpenHelper mOpenHelper;
    @Override
    protected int setContentView() {
        return R.layout.fragment_mapping_data_admin;
    }

    @Override
    protected void initView() {
        ll_noData= (LinearLayout) findViewById(R.id.fragment_mapping_data_ll_no_data);
        mListView= (ListView) findViewById(R.id.fragment_mapping_data_lst_view);

    }

    @Override
    protected void initData(Bundle arguments) {
        mAdapter=new MappingDataListAdapter(
                mList,
                getContext(),
                new MappingDataEditCallBack() {
                    @Override
                    public void onDelete(MappingData data) {
                        showToast("delete");
                    }

                    @Override
                    public void onEdit(MappingData data) {
                        showToast("edit");

                    }

                    @Override
                    public void onViewDetails(MappingData data) {
                        showToast("view details");

                    }
                }
        );
        mListView.setDividerHeight(0);
        mListView.setAdapter(mAdapter);

        mOpenHelper=new MappingDataOpenHelper(getContext());
        ArrayList<MappingData> dataList = mOpenHelper.getMappingDatas();
        if(dataList.size()>0){
            mList.addAll(dataList);
            updateListView();
        }


    }

    private void updateListView() {
        mAdapter.notifyDataSetChanged();
        mListView.smoothScrollToPosition(Integer.MAX_VALUE);
        if(mList.size()>0){
            ll_noData.setVisibility(View.GONE);
        }else {
            ll_noData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initListeners() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showToast("item clicked");
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOpenHelper.close();
    }
}
