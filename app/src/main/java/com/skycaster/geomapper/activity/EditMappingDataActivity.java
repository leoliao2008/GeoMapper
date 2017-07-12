package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActionBarActivity;
import com.skycaster.geomapper.bean.MappingData;
import com.skycaster.geomapper.data.StartMode;

public class EditMappingDataActivity extends BaseActionBarActivity {
    public static final String MAPPING_DATA="mapping_data";
    public static final String START_MODE="start_mode";
    private Intent mIntent;
    private StartMode mStartMode;


    public static void start(Context context, MappingData data, StartMode mode) {
        Intent starter = new Intent(context, EditMappingDataActivity.class);
        starter.putExtra(MAPPING_DATA,data);
        starter.putExtra(START_MODE,mode);
        context.startActivity(starter);
    }

    @Override
    protected int setRootViewLayout() {
        return R.layout.activity_edit_mapping_data;
    }

    @Override
    protected void initChildViews() {

    }

    @Override
    protected void initRegularData() {
        mIntent = getIntent();
        if(mIntent!=null){
            mStartMode= (StartMode) mIntent.getSerializableExtra(START_MODE);
        }

    }

    @Override
    protected int getActionBarTitle() {
        int title= R.string.mapping_data;
        if(mStartMode!=null&&mStartMode==StartMode.START_MODE_EDIT){
            title=R.string.edit_mapping_data;
        }
        return title;
    }

    @Override
    protected void initListeners() {

    }
}
