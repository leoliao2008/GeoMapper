package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.base.MyBaseAdapter;
import com.skycaster.geomapper.bean.Location;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/14.
 */

public class LocationListAdapter extends MyBaseAdapter<Location> {
    public LocationListAdapter(ArrayList<Location> list, Context context) {
        super(list, context, R.layout.item_dialog_location_selection_item);
    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, Location item) {

    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, Location item, int position) {
        ViewHolder h= (ViewHolder) viewHolder;
        h.tv_title.setText(item.getTitle());
        h.tv_lat.setText(context.getString(R.string.latitude)+item.getLatitude()+"°");
        h.tv_lng.setText(context.getString(R.string.longitude)+item.getLongitude()+"°");
        h.tv_date.setText(item.getSubmitDate());
        int imageRsr;
        switch (item.getIconStyle()){
            case 0:
                imageRsr=R.drawable.ic_pin_1;
                break;
            case 1:
                imageRsr=R.drawable.ic_pin_2;
                break;
            case 2:
                imageRsr=R.drawable.ic_pin_3;
                break;
            case 3:
                imageRsr=R.drawable.ic_pin_4;
                break;
            case 4:
                imageRsr=R.drawable.ic_pin_5;
                break;
            case 5:
            default:
                imageRsr=R.drawable.ic_pin_6;
                break;
        }
        h.iv_icon.setImageResource(imageRsr);
    }

    @Override
    protected BaseViewHolder instantiateViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    private class ViewHolder extends BaseViewHolder{
        private ImageView iv_icon;
        private TextView tv_title;
        private TextView tv_lat;
        private TextView tv_lng;
        private TextView tv_date;

        public ViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        protected void initViews(View convertView) {
            iv_icon= (ImageView) findViewById(R.id.item_dialog_location_selection_iv_icon);
            tv_title= (TextView) findViewById(R.id.item_dialog_location_selection_tv_title);
            tv_lat= (TextView) findViewById(R.id.item_dialog_location_selection_tv_lat);
            tv_lng= (TextView) findViewById(R.id.item_dialog_location_selection_tv_lng);
            tv_date= (TextView) findViewById(R.id.item_dialog_location_selection_tv_save_date);

        }
    }
}
