package com.skycaster.geomapper.adapterr;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.base.MyBaseAdapter;
import com.skycaster.geomapper.data.RouteIndexOpenHelper;
import com.skycaster.geomapper.interfaces.RouteRecordSelectedListener;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/22.
 */

public class RouteAdminAdapter extends MyBaseAdapter<String> {
    private RouteIndexOpenHelper mHelper;
    private RouteRecordSelectedListener mListener;
    public RouteAdminAdapter(ArrayList<String> list,Context context, RouteIndexOpenHelper helper, RouteRecordSelectedListener listener) {
        super(list, context, R.layout.item_route_admin);
        mHelper=helper;
        mListener=listener;
    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, final String item) {
        MyViewHolder vh= (MyViewHolder) viewHolder;
        vh.tv_routeName.setText(item);
        vh.tv_routeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRouteRecordSelected(item);
            }
        });
        vh.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHelper.deleteRoute(item)){
                    getList().remove(item);
                    notifyDataSetChanged();
                }
                if(getList().size()==0){
                    mListener.onRouteRecordEmpty();
                }
            }
        });

    }

    @Override
    protected BaseViewHolder instantiateViewHolder(View convertView) {
        return new MyViewHolder(convertView);
    }

    private class MyViewHolder extends BaseViewHolder{
        private TextView tv_routeName;
        private ImageView iv_delete;

        public MyViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        protected void initViews(View convertView) {
            tv_routeName= (TextView) convertView.findViewById(R.id.item_route_admin_tv_route_name);
            iv_delete= (ImageView) convertView.findViewById(R.id.item_route_admin_iv_delete);
        }
    }
}
