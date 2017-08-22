package com.skycaster.geomapper.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycaster.geomapper.R;

/**
 * Created by 廖华凯 on 2017/8/22.
 */

public class FileBrowserViewHolder extends RecyclerView.ViewHolder {
    private ImageView iv_icon;
    private TextView tv_name;
    private View rootView;
    public FileBrowserViewHolder(View itemView) {
        super(itemView);
        rootView=itemView;
        iv_icon= (ImageView) itemView.findViewById(R.id.item_file_browser_iv_icon);
        tv_name= (TextView) itemView.findViewById(R.id.item_file_browser_tv_name);
    }

    public ImageView getIv_icon() {
        return iv_icon;
    }

    public TextView getTv_name() {
        return tv_name;
    }

    public View getRootView() {
        return rootView;
    }
}
