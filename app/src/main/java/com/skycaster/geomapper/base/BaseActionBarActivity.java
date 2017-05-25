package com.skycaster.geomapper.base;

import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by 廖华凯 on 2017/5/25.
 */

public abstract class BaseActionBarActivity extends BaseActivity {
    private ActionBar mActionBar;
    @Override
    protected void initData() {
        initRoutineData();
        mActionBar=getSupportActionBar();
        mActionBar.setTitle(getResources().getString(getActionBarTitle()));
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    protected abstract int getActionBarTitle();

    protected abstract void initRoutineData();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return true;
    }
}
