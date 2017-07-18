package com.skycaster.geomapper.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseViewHolder;

/**
 * Created by 廖华凯 on 2017/7/18.
 */

public class ExpListViewGroupViewHolder extends BaseViewHolder {
    private TextView tv_tagName;
    private TextView tv_count;

    public ExpListViewGroupViewHolder(View convertView) {
        super(convertView);
    }

    @Override
    protected void initViews(View convertView) {
        tv_tagName= (TextView) findViewById(R.id.item_loc_record_group_view_tv_tag_name);
        tv_count= (TextView) findViewById(R.id.item_loc_record_group_view_tv_child_count);
    }

    public TextView getTv_tagName() {
        return tv_tagName;
    }

    public TextView getTv_count() {
        return tv_count;
    }
}
