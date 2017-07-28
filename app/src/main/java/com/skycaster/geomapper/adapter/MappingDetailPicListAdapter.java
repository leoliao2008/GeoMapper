package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.base.MyBaseAdapter;
import com.skycaster.geomapper.util.ImageUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/20.
 */

public class MappingDetailPicListAdapter extends MyBaseAdapter<String> {
    public MappingDetailPicListAdapter(ArrayList<String> list, Context context) {
        super(list, context, R.layout.item_mapping_detail_pic_list_item);
    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, String item) {
        ViewHolder vh= (ViewHolder) viewHolder;
        ImageUtil.LoadImageWithGlide(vh.mImageView,item);

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
