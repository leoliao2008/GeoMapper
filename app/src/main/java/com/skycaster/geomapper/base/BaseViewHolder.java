package com.skycaster.geomapper.base;

import android.view.View;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public abstract class BaseViewHolder {
    private View convertView;
    public BaseViewHolder(View convertView) {
        this.convertView=convertView;
        initViews(convertView);
    }

    protected abstract void initViews(View convertView);

    protected View findViewById(int id){
        return convertView.findViewById(id);
    }
}
