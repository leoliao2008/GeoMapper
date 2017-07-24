package com.skycaster.geomapper.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.util.AlertDialogUtil;
import com.skycaster.geomapper.util.ImageUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/28.
 */

public class LocationPicListAdapter extends BaseAdapter {

    private ArrayList<String>mList;
    private Activity mContext;
    private int picWidth;
    private RequestOptions mOptions;

    public LocationPicListAdapter(ArrayList<String> list, Activity context) {
        mList = list;
        mContext = context;
        picWidth= (int) (BaseApplication.getDisplayMetrics().widthPixels*0.9);
        mOptions=new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).downsample(DownsampleStrategy.AT_LEAST).centerCrop();
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
            int width = BaseApplication.getDisplayMetrics().widthPixels;
            float height = ImageUtil.calculateHeight(path, width);
            if(height>0){
                mOptions.override(width, (int) (height+0.5f));
                Glide.with(mContext).asBitmap().apply(mOptions).load(path).into(vh.ivPhoto);
            }else {
                vh.ivPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                vh.ivPhoto.setImageResource(R.drawable.pic_file_deleted);
            }

//            vh.ivPhoto.setImageBitmap(ImageUtil.getFixedWidthBitmap(path,picWidth));
            vh.ivPhoto.setFocusable(false);
            vh.ivPhoto.setOnClickListener(null);
            vh.ivDelete.setVisibility(View.VISIBLE);
            vh.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialogUtil.showHint(
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
