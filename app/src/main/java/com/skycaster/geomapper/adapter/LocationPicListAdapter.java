package com.skycaster.geomapper.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.util.AlertDialogUtil;
import com.skycaster.geomapper.util.ImageUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/28.
 */

public class LocationPicListAdapter extends BaseAdapter {

    private ArrayList<String>mList;
    private Activity mContext;

    public LocationPicListAdapter(ArrayList<String> list, Activity context) {
        mList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size()+1;
    }

    @Override
    public Object getItem(int position) {
        if(position<=mList.size()-1){
            return mList.get(position);
        }else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.item_location_pic_list,null);
            vh=new ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }
        if(position!=mList.size()){
            final String path = mList.get(position);
            ImageUtil.LoadImageWithGlide(vh.ivPhoto,path);
            vh.ivPhoto.setFocusable(false);
            vh.ivPhoto.setOnClickListener(null);
            vh.ivDelete.setVisibility(View.VISIBLE);
            vh.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialogUtil.showStandardDialog(
                            mContext,
                            mContext.getString(R.string.warning_delete_loc_photo),
                            new Runnable() {
                                @Override
                                public void run() {
                                    mList.remove(path);
                                    notifyDataSetChanged();
                                }
                            },
                            new Runnable() {
                                @Override
                                public void run() {
                                    //do nothing
                                }
                            }
                    );
                }
            });
        }else {
            vh.ivDelete.setVisibility(View.GONE);
            vh.ivPhoto.setFocusable(true);
            vh.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            vh.ivPhoto.setImageResource(R.drawable.selector_ic_add);
            vh.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialogUtil.showChooseImageSourceDialog(mContext);
                }
            });

        }
        return convertView;
    }

    private class ViewHolder{
        ImageView ivPhoto;
        ImageView ivDelete;

        public ViewHolder(View convertView) {
            ivPhoto = (ImageView) convertView.findViewById(R.id.item_loc_pic_list_image);
            ivDelete= (ImageView) convertView.findViewById(R.id.item_loc_pic_list_iv_delete);
        }
    }
}
