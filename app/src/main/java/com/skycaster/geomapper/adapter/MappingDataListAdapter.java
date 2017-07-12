package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.base.MyBaseAdapter;
import com.skycaster.geomapper.bean.MappingData;
import com.skycaster.geomapper.bean.MyLatLng;
import com.skycaster.geomapper.interfaces.MappingDataEditCallBack;
import com.skycaster.geomapper.util.LogUtil;
import com.skycaster.geomapper.util.NetUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/12.
 */

public class MappingDataListAdapter extends MyBaseAdapter<MappingData> {
    private MappingDataEditCallBack mCallBack;
    public MappingDataListAdapter(ArrayList<MappingData> list, Context context,MappingDataEditCallBack callBack) {
        super(list, context, R.layout.item_mapping_data);
        mCallBack=callBack;
    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, final MappingData item) {
        ViewHolder h= (ViewHolder) viewHolder;
        h.tv_title.setText(item.getTitle());
        h.tv_date.setText(item.getDate());
        h.tv_adjacent.setText(item.getAdjacentLoc());
        h.tv_area.setText(String.format("%.02f",item.getArea()));
        h.tv_perimeter.setText(String.format("%.02f",item.getPerimeter()));
        MyLatLng latLng = item.getLatLngs().get(0);
        String url = NetUtil.generatePanoramaUrl(
                latLng.getLat(),
                latLng.getLng(),
                512,
                256);
        LogUtil.showLog("-------------",url);
        Glide.with(context).asBitmap().load(url).into(h.iv_panorama);
        h.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onDelete(item);
            }
        });
        h.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onEdit(item);
            }
        });
        h.iv_toDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onViewDetails(item);
            }
        });
    }

    @Override
    protected BaseViewHolder instantiateViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    private class ViewHolder extends BaseViewHolder{
        private TextView tv_adjacent;
        private TextView tv_title;
        private TextView tv_perimeter;
        private TextView tv_area;
        private TextView tv_date;
        private ImageView iv_edit;
        private ImageView iv_delete;
        private ImageView iv_toDetail;
        private ImageView iv_panorama;

        public ViewHolder(View convertView) {
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
    }
}
