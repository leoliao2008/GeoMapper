package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.presenters.Sk9042ControlPanelPresenter;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2018/5/17.
 */

public class SK9042SettingActivity extends BaseActionBarActivity {

    private ListView mControlPanel;
    private ListView mDisplayPanel;
    private ArrayList<String> mMsg=new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private Sk9042ControlPanelPresenter mPresenter;


    public static void start(Context context) {
        Intent starter = new Intent(context, SK9042SettingActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected String setActionBarTitle() {
        return getString(R.string.sk9042_module_setting);
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_sk9042_setting;
    }

    @Override
    protected void initChildViews() {
        mControlPanel= (ListView) findViewById(R.id.control_panel);
        mDisplayPanel= (ListView) findViewById(R.id.display_console);

    }

    @Override
    protected void initData() {
        mAdapter = new ArrayAdapter<String>(
                this,
                R.layout.item_simple_list_view_item,
                mMsg
        );
        mDisplayPanel.setAdapter(mAdapter);

        mPresenter=new Sk9042ControlPanelPresenter(this);
        mPresenter.init();
    }

    @Override
    protected void initListeners() {

    }

    public ListView getControlPanel() {
        return mControlPanel;
    }

    public void updateDisplayConsole(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMsg.add(msg);
                if(mMsg.size()>50){
                    mMsg.remove(0);
                }
                mAdapter.notifyDataSetChanged();
                mDisplayPanel.smoothScrollToPosition(Integer.MAX_VALUE);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isFinishing()){
            mPresenter.onDestroy();
        }
    }
}
