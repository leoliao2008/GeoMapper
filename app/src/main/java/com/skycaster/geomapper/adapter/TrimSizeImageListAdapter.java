package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.base.MyBaseAdapter;
import com.skycaster.geomapper.interfaces.PicListAdapterCallBack;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/18.
 */

public class TrimSizeImageListAdapter extends MyBaseAdapter<String> {

    private final RequestOptions mOptions;
    private PicListAdapterCallBack mCallBack;

    public TrimSizeImageListAdapter(ArrayList<String> list, Context context, PicListAdapterCallBack callBack) {
        super(list, context, R.layout.item_pic_list_fixed_height);
        mOptions = new RequestOptions();
        mOptions.encodeQuality(50)
                .centerCrop()
                .downsample(DownsampleStrategy.AT_LEAST)
                .error(R.drawable.pic_file_deleted)
                .override(BaseApplication.getDisplayMetrics().widthPixels)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        mCallBack=callBack;
    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, String item) {
    }

    @Override
    public int getCount() {
        return getList().size()+1;
    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, final String item, final int position) {
        ViewHolder vh= (ViewHolder) viewHolder;
        if(position!=getCount()-1){
            vh.iv_add.setVisibility(View.GONE);
            vh.iv_pic.setVisibility(View.VISIBLE);
            vh.iv_delete.setVisibility(View.VISIBLE);
//            Bitmap bitmap = ImageUtil.getFixedWidthBitmap(item, 256);
//            if(bitmap!=null){
//                vh.iv_pic.setImageBitmap(bitmap);
//            }else {
//                vh.iv_pic.setImageResource(R.drawable.pic_file_deleted);
//            }
            Glide.with(context).asBitmap().apply(mOptions).load(item).into(vh.iv_pic);
            vh.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onDeletePic(item,position);
                }
            });
        }else {
            vh.iv_add.setVisibility(View.VISIBLE);
            vh.iv_pic.setVisibility(View.GONE);
            ((ViewHolder) viewHolder).iv_delete.setVisibility(View.GONE);
            vh.iv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onAddPic();
                }
            });
        }

    }

    @Override
    protected BaseViewHolder instantiateViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    private class ViewHolder extends BaseViewHolder{
        private ImageView iv_add;
        private ImageView iv_delete;
        private ImageView iv_pic;

        public ViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        protected void initViews(View convertView) {
            iv_add= (ImageView) findViewById(R.id.item_pic_list_iv_add);
            iv_delete= (ImageView) findViewById(R.id.item_pic_list_iv_delete);
            iv_pic= (ImageView) findViewById(R.id.item_pic_list_iv_pic);
        }
    }
}