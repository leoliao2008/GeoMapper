package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.base.MyBaseAdapter;
import com.skycaster.geomapper.bean.LocationTag;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/28.
 */

public class LocTagListAdapter extends MyBaseAdapter<LocationTag> {
    public LocTagListAdapter(ArrayList<LocationTag> list, Context context) {
        super(list, context, R.layout.item_loc_tag);
    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, LocationTag item) {
        ViewHolder vh= (ViewHolder) viewHolder;
        vh.tvTagName.setText(context.getString(R.string.name)+"："+item.getTagName());
        vh.tvTagId.setText("ID："+item.getId());
    }

    @Override
    protected BaseViewHolder instantiateViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    private class ViewHolder extends BaseViewHolder{
        private TextView tvTagName;
        private TextView tvTagId;

        public ViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        protected void initViews(View convertView) {
            tvTagName= (TextView) convertView.findViewById(R.id.item_loc_tag_name);
            tvTagId= (TextView) convertView.findViewById(R.id.item_loc_tag_id);
        }

    }
}
