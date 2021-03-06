package com.skycaster.geomapper.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.PicIteratorActivity;
import com.skycaster.geomapper.util.ImageUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/28.
 */

public class FullSizeImageListAdapter extends BaseAdapter {

    private ArrayList<String>mList;
    private Activity mContext;

    public FullSizeImageListAdapter(ArrayList<String> list, Activity context) {
        mList = list;
        mContext = context;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.item_fresco_pic,null);
            vh=new ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }
        final String path = mList.get(position);
//        ImageUtil.LoadImageWithGlide(vh.ivPhoto,path);
        ImageUtil.LoadImageWidthFresco(vh.ivPhoto,path);
        vh.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PicIteratorActivity.start(mContext,mList,position);
            }
        });
        return convertView;
    }

//    private class ViewHolder{
//        ImageView ivPhoto;
//        ImageView ivDelete;
//
//        public ViewHolder(View convertView) {
//            ivPhoto = (ImageView) convertView.findViewById(R.id.item_loc_pic_list_image);
//            ivDelete= (ImageView) convertView.findViewById(R.id.item_loc_pic_list_iv_delete);
//            ivDelete.setVisibility(View.GONE);
//        }
//    }

    private class ViewHolder{
        SimpleDraweeView ivPhoto;
        ImageView ivDelete;

        public ViewHolder(View convertView) {
            ivPhoto = (SimpleDraweeView) convertView.findViewById(R.id.item_fresco_pic_simple_drawee_view);
            ivDelete= (ImageView) convertView.findViewById(R.id.item_fresco_pic_iv_delete);
            ivDelete.setVisibility(View.GONE);
        }
    }
}
