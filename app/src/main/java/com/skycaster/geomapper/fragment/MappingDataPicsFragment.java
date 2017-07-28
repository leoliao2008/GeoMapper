package com.skycaster.geomapper.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.PicIteratorActivity;
import com.skycaster.geomapper.adapter.TrimSizeImageListAdapter;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.base.BaseFragment;
import com.skycaster.geomapper.bean.MappingData;
import com.skycaster.geomapper.data.Constants;
import com.skycaster.geomapper.interfaces.PicListAdapterCallBack;
import com.skycaster.geomapper.interfaces.RequestTakingPhotoCallback;
import com.skycaster.geomapper.util.AlertDialogUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/18.
 */

public class MappingDataPicsFragment extends BaseFragment {
    private ListView mListView;
    private ArrayList<String> mPaths=new ArrayList<>();
    private TrimSizeImageListAdapter mAdapter;

    public MappingDataPicsFragment(@Nullable MappingData source) {
        Bundle bundle=new Bundle();
        bundle.putParcelable(Constants.MAPPING_DATA_SOURCE,source);
        setArguments(bundle);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_pic_list;
    }

    @Override
    protected void initView() {
        mListView= (ListView) findViewById(R.id.fragment_pic_list_lst_view);

    }

    @Override
    protected void initData(Bundle arguments) {
        MappingData data=arguments.getParcelable(Constants.MAPPING_DATA_SOURCE);
        if(data!=null){
            mPaths.addAll(data.getPicPaths());
        }
        mAdapter=new TrimSizeImageListAdapter(mPaths, getContext(), new PicListAdapterCallBack() {
            @Override
            public void onDeletePic(String item, int position) {
                if(mPaths.remove(item)){
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onAddPic() {
                AlertDialogUtil.showChooseImageSourceDialog(MappingDataPicsFragment.this);

            }
        });
        mListView.setDividerHeight(0);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void initListeners() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PicIteratorActivity.start(getContext(),mPaths,position);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AlertDialogUtil.onActivityResult(requestCode, resultCode, data, new RequestTakingPhotoCallback() {
            @Override
            public void onPhotoTaken(String path) {
                updateListView(path);
            }
        });
    }

    private void updateListView(String newItem){
        mPaths.add(newItem);
        mAdapter.notifyDataSetChanged();
        BaseApplication.postDelay(new Runnable() {
            @Override
            public void run() {
                mListView.smoothScrollToPosition(Integer.MAX_VALUE);
            }
        },100);
    }

    public MappingData updateMappingData(MappingData data){
        data.setPicPaths(mPaths);
        return data;
    }
}
