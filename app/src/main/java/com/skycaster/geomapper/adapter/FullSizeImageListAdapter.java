package com.skycaster.geomapper.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

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

    public FullSizeImageListAdapter(ArrayList<String> list, Activity context) {
        mList = list;
        mContext = context;
        picWidth= (int) (BaseApplication.getDisplayMetrics().widthPixels*0.8);
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
        ViewHolder vh;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.item_location_pic_list,null);
            vh=new ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }
        final String path = mList.get(position);
        vh.ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Bitmap bitmap = ImageUtil.getFixedWidthBitmap(path, picWidth);
        if(bitmap!=null){
            vh.ivPhoto.setImageBitmap(bitmap);
        }else {
            vh.ivPhoto.setImageResource(R.drawable.pic_file_deleted);
        }

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
