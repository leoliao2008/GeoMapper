package com.skycaster.geomapper.base;

import android.view.View;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public abstract class BaseViewHolder {
    public BaseViewHolder(View convertView) {
        initViews(convertView);
    }

    protected abstract void initViews(View convertView);
}
