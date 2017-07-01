package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.bean.LocRecordGroupItem;
import com.skycaster.geomapper.bean.Location;
import com.skycaster.geomapper.interfaces.LocRecordEditCallBack;
import com.skycaster.geomapper.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/30.
 */

public class LocationListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<LocRecordGroupItem> mGroupList;
    private LocRecordEditCallBack mCallBack;

    public LocationListAdapter(Context context, ArrayList<LocRecordGroupItem> groupList,LocRecordEditCallBack callBack) {
        mContext = context;
        mGroupList=groupList;
        mCallBack=callBack;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroupList.get(groupPosition).getLocations().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroupList.get(groupPosition).getLocations().get(childPosition);
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
        GroupViewHolder viewHolder;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.item_laoction_records_group_view,null);
            viewHolder=new GroupViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (GroupViewHolder) convertView.getTag();
        }
        LocRecordGroupItem groupItem = mGroupList.get(groupPosition);
        viewHolder.tvTagName.setText(groupItem.getLocationTag().getTagName());
        viewHolder.tvChildCount.setText(String.valueOf(groupItem.getLocations().size()));
//        viewHolder.mCheckBox.setChecked(isExpanded);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.item_location_records_child_view,null);
            viewHolder=new ChildViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ChildViewHolder) convertView.getTag();
        }
        final Location location = mGroupList.get(groupPosition).getLocations().get(childPosition);
        LogUtil.showLog("location detail:   ",location.toString());
        viewHolder.tv_title.setText(location.getTitle());
        viewHolder.tv_coordinate.setText(location.getLatitude()+"N° "+location.getLongitude()+"E° "+location.getAltitude()+"M");
        viewHolder.tv_submitDate.setText(location.getSubmitDate());
        int imageSrc=-1;
        switch (location.getIconStyle()){
            case 0:
                imageSrc=R.drawable.ic_pin_1;
                break;
            case 1:
                imageSrc=R.drawable.ic_pin_2;
                break;
            case 2:
                imageSrc=R.drawable.ic_pin_3;
                break;
            case 3:
                imageSrc=R.drawable.ic_pin_4;
                break;
            case 4:
                imageSrc=R.drawable.ic_pin_5;
                break;
            case 5:
                imageSrc=R.drawable.ic_pin_6;
                break;
        }
        if(imageSrc!=-1){
            viewHolder.iv_icon.setImageResource(imageSrc);
        }
        viewHolder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onEdit(location);
            }
        });
        viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onDelete(location);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return groupId*100+childId;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return groupId;
    }

//    private void animateChildViewExpanding(final View convertView,int startHeight,int stopHeight){
//        final ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
//        ValueAnimator animator=ValueAnimator.ofInt(startHeight,stopHeight);
//        animator.setDuration(500);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int value = (int) animation.getAnimatedValue();
//                layoutParams.height=value;
//                convertView.setLayoutParams(layoutParams);
//                convertView.requestLayout();
//            }
//        });
//    }

    private class GroupViewHolder extends BaseViewHolder{
        private TextView tvTagName;
        private TextView tvChildCount;
//        private CheckBox mCheckBox;

        public GroupViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        protected void initViews(View convertView) {
            tvTagName= (TextView) convertView.findViewById(R.id.item_loc_record_group_view_tv_tag_name);
            tvChildCount= (TextView) convertView.findViewById(R.id.item_loc_record_group_view_tv_child_count);
//            mCheckBox= (CheckBox) convertView.findViewById(R.id.item_loc_record_group_view_cbx);
        }
    }

    private class ChildViewHolder extends BaseViewHolder{
        private ImageView iv_icon;
        private TextView tv_title;
        private TextView tv_coordinate;
        private TextView tv_submitDate;
        private ImageView iv_delete;
        private ImageView iv_edit;

        public ChildViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        protected void initViews(View convertView) {
            iv_icon= (ImageView) convertView.findViewById(R.id.item_location_record_child_view_iv_icon);
            tv_title= (TextView) convertView.findViewById(R.id.item_location_record_child_view_tv_title);
            tv_coordinate= (TextView) convertView.findViewById(R.id.item_location_record_child_view_tv_coordinate);
            tv_submitDate= (TextView) convertView.findViewById(R.id.item_location_record_child_view_tv_save_date);
            iv_delete= (ImageView) convertView.findViewById(R.id.item_location_record_child_view_iv_delete_record);
            iv_edit= (ImageView) convertView.findViewById(R.id.item_location_record_child_view_iv_edit_record);
        }
    }
}
