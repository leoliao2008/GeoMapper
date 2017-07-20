package com.skycaster.geomapper.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skycaster.geomapper.util.LogUtil;
import com.skycaster.geomapper.util.ToastUtil;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    protected View rootView;

    public BaseFragment(){}

    public BaseFragment(Context context,Bundle bundle) {
        mContext=context;
        setArguments(bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(setContentView(),null);
        initView();
        initData(getArguments());
        initListeners();
        return rootView;
    }


    protected abstract int setContentView();

    protected abstract void initView();

    protected abstract void initData(Bundle arguments);

    protected abstract void initListeners();

    protected View findViewById(int id){
        return rootView.findViewById(id);
    }

    protected void showLog(String msg){
        LogUtil.showLog(getClass().getSimpleName(), msg);
    }

    protected void showToast(String msg){
        ToastUtil.showToast(msg);
    }

}
