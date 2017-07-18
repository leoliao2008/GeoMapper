package com.skycaster.geomapper.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseViewHolder;

/**
 * Created by 廖华凯 on 2017/7/18.
 */

public class MappingDataItemDetailViewHolder extends BaseViewHolder {
    private TextView tv_adjacent;
    private TextView tv_title;
    private TextView tv_perimeter;
    private TextView tv_area;
    private TextView tv_date;
    private ImageView iv_edit;
    private ImageView iv_delete;
    private ImageView iv_toDetail;
    private ImageView iv_panorama;

    public MappingDataItemDetailViewHolder(View convertView) {
        super(convertView);
    }

    @Override
    protected void initViews(View convertView) {
        tv_adjacent= (TextView) findViewById(R.id.item_mapping_data_tv_adjacent_description);
        tv_title= (TextView) findViewById(R.id.item_mapping_data_tv_title);
        tv_perimeter= (TextView) findViewById(R.id.item_mapping_data_tv_perimeter);
        tv_area = (TextView) findViewById(R.id.item_mapping_data_tv_area);
        tv_date= (TextView) findViewById(R.id.item_mapping_data_tv_date);
        iv_panorama= (ImageView) findViewById(R.id.item_mapping_data_iv_panorama_view);
        iv_edit= (ImageView) findViewById(R.id.item_mapping_data_iv_edit);
        iv_delete= (ImageView) findViewById(R.id.item_mapping_data_iv_delete);
        iv_toDetail= (ImageView) findViewById(R.id.item_mapping_data_iv_next);

    }

    public TextView getTv_adjacent() {
        return tv_adjacent;
    }

    public TextView getTv_title() {
        return tv_title;
    }

    public TextView getTv_perimeter() {
        return tv_perimeter;
    }

    public TextView getTv_area() {
        return tv_area;
    }

    public TextView getTv_date() {
        return tv_date;
    }

    public ImageView getIv_edit() {
        return iv_edit;
    }

    public ImageView getIv_delete() {
        return iv_delete;
    }

    public ImageView getIv_toDetail() {
        return iv_toDetail;
    }

    public ImageView getIv_panorama() {
        return iv_panorama;
    }
}
