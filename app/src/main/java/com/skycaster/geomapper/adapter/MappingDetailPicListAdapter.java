package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.base.MyBaseAdapter;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/20.
 */

public class MappingDetailPicListAdapter extends MyBaseAdapter<String> {
    private RequestOptions mOptions;
    public MappingDetailPicListAdapter(ArrayList<String> list, Context context) {
        super(list, context, R.layout.item_mapping_detail_pic_list_item);
        mOptions=new RequestOptions().downsample(DownsampleStrategy.AT_LEAST).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, String item) {
        ViewHolder vh= (ViewHolder) viewHolder;
        Glide.with(context).asBitmap().apply(mOptions).load(item).into(vh.mImageView);
    }

    @Override
    protected BaseViewHolder instantiateViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    private class ViewHolder extends BaseViewHolder{
        private ImageView mImageView;

        public ViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        protected void initViews(View convertView) {
            mImageView= (ImageView) convertView.findViewById(R.id.item_mapping_pic_iv_pic);
        }
    }
}
