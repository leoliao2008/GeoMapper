package com.skycaster.geomapper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.AddLocationActivity;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.bean.Location;
import com.skycaster.geomapper.interfaces.CoordinateListEditCallback;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/7.
 */

public class MappingCoordinateListAdapter extends BaseAdapter {
    private ArrayList<Location> list;
    private Context mContext;
    private CoordinateListEditCallback callback;

    public MappingCoordinateListAdapter(ArrayList<Location> list, Context context,CoordinateListEditCallback callBack) {
        this.list=list;
        mContext=context;
        this.callback=callBack;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.item_mapping_coordinate,null);
            h=new ViewHolder(convertView);
            convertView.setTag(h);
        }else {
            h= (ViewHolder) convertView.getTag();
        }
        final Location location = list.get(position);
        h.tv_index.setText(String.format("%02d",position+1));
        h.tv_lng.setText(String.valueOf(location.getLongitude()));
        h.tv_lat.setText(String.valueOf(location.getLatitude()));
        h.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onInsertNewLocation(position+1);
            }
        });
        h.iv_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onRemove(location);

            }
        });
        h.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onEdit(location);
            }
        });

        h.iv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddLocationActivity.startForResult((Activity) mContext,1234,location);
            }
        });


        return convertView;
    }

    private class ViewHolder extends BaseViewHolder{
        private TextView tv_index;
        private TextView tv_lat;
        private TextView tv_lng;
        private ImageView iv_add;
        private ImageView iv_remove;
        private ImageView iv_edit;
        private ImageView iv_save;

        public ViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        protected void initViews(View convertView) {
            tv_index= (TextView) convertView.findViewById(R.id.item_mapping_coordinate_tv_index);
            tv_lat= (TextView) convertView.findViewById(R.id.item_mapping_coordinate_tv_lat);
            tv_lng= (TextView) convertView.findViewById(R.id.item_mapping_coordinate_tv_lng);
            iv_add= (ImageView) convertView.findViewById(R.id.item_mapping_coordinate_iv_add);
            iv_remove= (ImageView) convertView.findViewById(R.id.item_mapping_coordinate_iv_remove);
            iv_edit= (ImageView) convertView.findViewById(R.id.item_mapping_coordinate_iv_edit);
            iv_save= (ImageView) convertView.findViewById(R.id.item_mapping_coordinate_iv_save);

        }
    }
}
