package com.skycaster.geomapper.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.util.ImageUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/28.
 */

public class FullSizeImageListAdapter extends BaseAdapter {

    private ArrayList<String>mList;
    private Activity mContext;
    private int picWidth;
    private RequestOptions mOptions;

    public FullSizeImageListAdapter(ArrayList<String> list, Activity context) {
        mList = list;
        mContext = context;
        picWidth= BaseApplication.getDisplayMetrics().widthPixels;
        mOptions=new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).downsample(DownsampleStrategy.AT_LEAST)
                .placeholder(R.drawable.pic_file_deleted).error(R.drawable.pic_file_deleted).encodeQuality(10);

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.item_location_pic_list,null);
            vh=new ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }
        final String path = mList.get(position);
        float height = ImageUtil.calculateHeight(path, picWidth);
        Glide.with(mContext).asBitmap().apply(mOptions.override(picWidth, (int) (height+0.5f))).load(path)
                .transition(BitmapTransitionOptions.withCrossFade()).into(vh.ivPhoto);

        return convertView;
    }

    private class ViewHolder{
        ImageView ivPhoto;
        ImageView ivDelete;

        public ViewHolder(View convertView) {
            ivPhoto = (ImageView) convertView.findViewById(R.id.item_loc_pic_list_image);
            ivDelete= (ImageView) convertView.findViewById(R.id.item_loc_pic_list_iv_delete);
            ivDelete.setVisibility(View.GONE);
        }
    }
}
