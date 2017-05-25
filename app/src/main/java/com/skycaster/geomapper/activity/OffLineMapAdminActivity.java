package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;

public class OffLineMapAdminActivity extends BaseActionBarActivity {
    private RecyclerView mRecyclerView;

    public static void startActivity(Context context){
        context.startActivity(new Intent(context,OffLineMapAdminActivity.class));
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_off_line_map_admin;
    }

    @Override
    protected void initChildViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_off_line_map_admin_recycler_view);

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected int getActionBarTitle() {
        return R.string.admin_off_line_map;
    }

    @Override
    protected void initRoutineData() {

    }
}
