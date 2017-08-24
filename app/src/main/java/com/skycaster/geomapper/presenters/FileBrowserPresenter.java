package com.skycaster.geomapper.presenters;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.skycaster.geomapper.activity.FileBrowserActivity;
import com.skycaster.geomapper.adapter.FileBrowserAdapter;
import com.skycaster.geomapper.models.FileOpenerModel;
import com.skycaster.geomapper.models.LocalStorageModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/8/22.
 */

public class FileBrowserPresenter {

    private FileBrowserActivity mActivity;
    private TextView tv_path;
    private RecyclerView mRecyclerView;
    private ArrayList<File> mList =new ArrayList<>();
    private GridLayoutManager mGridLayoutManager;
    private LocalStorageModel mLocalStorageModel;
    private ArrayList<File> browsHistory=new ArrayList<>();
    private FileBrowserAdapter mAdapter;
    private FileOpenerModel mFileOpenerModel;

    public FileBrowserPresenter(FileBrowserActivity activity) {
        mActivity = activity;
        tv_path=mActivity.getTv_currentPath();
        mRecyclerView =mActivity.getRcy_fileList();
        mLocalStorageModel=new LocalStorageModel();
        mFileOpenerModel=new FileOpenerModel();
    }

    public void init(){
        boolean sdCardAvailable = mLocalStorageModel.isSdCardAvailable();
        if(sdCardAvailable){
            browsHistory.add(mLocalStorageModel.getDownLoadDir());
        }else {
            browsHistory.add(mLocalStorageModel.getDataDir());
        }
        initRecyclerView();
        updateRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter=new FileBrowserAdapter(mList,mActivity);
        mGridLayoutManager=new GridLayoutManager(mActivity,5, LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new FileBrowserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                File file = mList.get(position);
                if(file.isDirectory()){
                    browsHistory.add(file);
                    updateRecyclerView();
                }else {
                    mFileOpenerModel.openFile(mActivity,file);
                }
            }
        });
    }

    private void updateRecyclerView() {
        File currentDir = browsHistory.get(browsHistory.size() - 1);
        if(!currentDir.exists()){
            currentDir.mkdirs();
        }
        File[] listFiles = currentDir.listFiles();
        mList.clear();
        if(listFiles!=null&&listFiles.length>0){
            for(File file:listFiles){
                mList.add(file);
            }
        }

        mAdapter.notifyDataSetChanged();

        tv_path.setText(currentDir.getAbsolutePath());
    }

    public void back(){
        if(browsHistory.size()<2){
            return;
        }
        File top = browsHistory.get(browsHistory.size() - 1);
        browsHistory.remove(top);
        updateRecyclerView();
        int index = mList.indexOf(top);
        mRecyclerView.scrollToPosition(index);
        View child = mRecyclerView.getChildAt(index);
        if(child!=null){
            child.setSelected(true);
        }
    }

    public void up(){
        File top = browsHistory.get(browsHistory.size() - 1);
        File parentFile = top.getParentFile();
        if(parentFile!=null&&parentFile.exists()){
            browsHistory.add(parentFile);
            updateRecyclerView();
        }
    }
}
