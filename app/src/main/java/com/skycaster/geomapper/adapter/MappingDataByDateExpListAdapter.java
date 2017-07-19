package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.bean.MapDataGroupByDate;
import com.skycaster.geomapper.bean.MappingData;
import com.skycaster.geomapper.bean.MyLatLng;
import com.skycaster.geomapper.interfaces.MappingDataEditCallBack;
import com.skycaster.geomapper.util.NetUtil;
import com.skycaster.geomapper.viewHolder.ExpListViewGroupViewHolder;
import com.skycaster.geomapper.viewHolder.MappingDataItemDetailViewHolder;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/18.
 */

public class MappingDataByDateExpListAdapter extends BaseExpandableListAdapter {
    private ArrayList<MapDataGroupByDate> groupList;
    private Context mContext;
    private RequestOptions mOptions;
    private MappingDataEditCallBack mCallBack;

    public MappingDataByDateExpListAdapter(Context context, ArrayList<MapDataGroupByDate> groupList, MappingDataEditCallBack callBack) {
        this.groupList = groupList;
        mContext = context;
        mOptions = new RequestOptions();
        mOptions.placeholder(R.drawable.pic_panorama_default_large).error(R.drawable.pic_panorama_default_large).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        mCallBack=callBack;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groupList.get(groupPosition).getList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groupList.get(groupPosition).getList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition*100+childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ExpListViewGroupViewHolder vh;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.item_location_records_group_view,null);
            vh=new ExpListViewGroupViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh= (ExpListViewGroupViewHolder) convertView.getTag();
        }
        MapDataGroupByDate temp = groupList.get(groupPosition);
        vh.getTv_count().setText(String.valueOf(temp.getList().size()));
        vh.getTv_tagName().setText(temp.getDate());
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        MappingDataItemDetailViewHolder vh;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.item_mapping_data,null);
            vh=new MappingDataItemDetailViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh= (MappingDataItemDetailViewHolder) convertView.getTag();
        }
        final MappingData item = groupList.get(groupPosition).getList().get(childPosition);
        vh.getTv_title().setText(item.getTitle());
        vh.getTv_date().setText(item.getDate());
        vh.getTv_adjacent().setText(item.getAdjacentLoc());
        vh.getTv_area().setText(String.format("%.02f",item.getArea()));
        vh.getTv_perimeter().setText(String.format("%.02f",item.getPerimeter()));
        MyLatLng latLng = item.getLatLngs().get(0);
        String url = NetUtil.generatePanoramaUrl(
                latLng.getLat(),
                latLng.getLng(),
                512,
                256);
        Glide.with(mContext).setDefaultRequestOptions(mOptions).asBitmap().load(url).into(vh.getIv_panorama());
        vh.getIv_delete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onDelete(item,groupPosition,childPosition);
            }
        });
        vh.getIv_edit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onEdit(item,groupPosition,childPosition);
            }
        });
        vh.getIv_toDetail().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onViewDetails(item,groupPosition,childPosition);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
