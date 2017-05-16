package com.skycaster.geomapper.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseActivity;

public class HomeActivity extends BaseActivity {

    private FloatingActionButton mFab;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void startActivity(Context context){
        context.startActivity(new Intent(context,HomeActivity.class));
    }

    @Override
    protected int setBaseLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("暂未分配点击事件。");
            }
        });

    }

    public void startTraceActivity(View view) {
        BaiduTraceActivity.startActivity(this);
    }
}
