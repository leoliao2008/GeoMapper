package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.util.AlertDialogUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/28.
 */

public class LocationPicListAdapter extends BaseAdapter {

    private ArrayList<Uri>mList;
    private Context mContext;

    public LocationPicListAdapter(ArrayList<Uri> list, Context context) {
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
            Uri uri = mList.get(position);
            Glide.with(mContext).load(uri).into(vh.mImageView);
            vh.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //// TODO: 2017/6/28
                }
            });
        }else {
            vh.mImageView.setImageResource(R.drawable.ic_acquire_image_resources);
            vh.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialogUtil.showChoosePicSourceDialog(mContext);
                }
            });

        }
        return convertView;
    }

    private class ViewHolder{
        ImageView mImageView;

        public ViewHolder(View convertView) {
            mImageView= (ImageView) convertView.findViewById(R.id.item_loc_pic_list_image);
        }
    }
}
