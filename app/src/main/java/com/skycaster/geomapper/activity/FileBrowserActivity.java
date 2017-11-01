package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.presenters.FileBrowserPresenter;

/**
 * Created by 廖华凯 on 2017/8/21.
 */

public class FileBrowserActivity extends BaseActionBarActivity {
    private TextView tv_currentPath;
    private RecyclerView rcy_fileList;
    private Button btn_up;
    private Button btn_back;
    private FileBrowserPresenter mPresenter;


    public static void start(Context context) {
        Intent starter = new Intent(context, FileBrowserActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected String setActionBarTitle() {
        return getResources().getString(R.string.browse_local_file);
    }

    @Override
    protected void initData() {
        mPresenter=new FileBrowserPresenter(this);
        mPresenter.init();
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_file_browser;
    }

    @Override
    protected void initChildViews() {
        tv_currentPath= (TextView) findViewById(R.id.activity_file_browser_tv_current_path);
        rcy_fileList= (RecyclerView) findViewById(R.id.activity_file_browser_recycler_view);
        btn_back= (Button) findViewById(R.id.activity_file_browser_btn_return);
        btn_up= (Button) findViewById(R.id.activity_file_browser_btn_up);

    }

    @Override
    protected void initListeners() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.back();
            }
        });

        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.up();
            }
        });

    }

    public TextView getTv_currentPath() {
        return tv_currentPath;
    }

    public RecyclerView getRcy_fileList() {
        return rcy_fileList;
    }
}
