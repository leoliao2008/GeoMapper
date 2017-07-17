package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.interfaces.CreateCoordinateCallBack;
import com.skycaster.geomapper.interfaces.CoordinateItemEditCallBack;
import com.skycaster.geomapper.util.AlertDialogUtil;
import com.skycaster.geomapper.util.NetUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/14.
 */

public class MappingCoordinateRecyclerAdapter extends RecyclerView.Adapter<MappingCoordinateRecyclerAdapter.ViewHolder> {
    private ArrayList<LatLng> list;
    private Context mContext;
    private CoordinateItemEditCallBack mCallback;

    public MappingCoordinateRecyclerAdapter(ArrayList<LatLng> list, Context context, CoordinateItemEditCallBack callback) {
        this.list = list;
        mContext = context;
        mCallback=callback;
        Fresco.initialize(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(mContext, R.layout.item_save_mapping_data_recycler_view,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_index.setText(String.format("%02d",position+1));
        if(position==getItemCount()-1){
            holder.iv_edit.setEnabled(false);
            holder.tv_lng.setVisibility(View.GONE);
            holder.tv_lat.setVisibility(View.GONE);
            holder.fl_rootView.setOnClickListener(null);
            holder.mDraweeView.setImageResource(R.drawable.selector_ic_add_48dp);
            holder.mDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position==getItemCount()-1){
                        AlertDialogUtil.showAddCoordinateDialog(mContext, new CreateCoordinateCallBack() {
                            @Override
                            public void onCoordinateCreated(LatLng location) {
                                mCallback.onInsertNewLatlng(position+1,location);
                            }
                        });
                    }
                }
            });

        }else {
            holder.mDraweeView.setOnClickListener(null);
            holder.iv_edit.setEnabled(true);
            holder.tv_lat.setVisibility(View.VISIBLE);
            holder.tv_lng.setVisibility(View.VISIBLE);
            final LatLng latLng = list.get(position);
            AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(NetUtil.generatePanoramaUrl(
                            latLng.latitude,
                            latLng.longitude,
                            mContext.getResources().getDimensionPixelSize(R.dimen.width_simple_drawee_view),
                            mContext.getResources().getDimensionPixelSize(R.dimen.height_simple_drawee_view)))
                    .setTapToRetryEnabled(true)
                    .setOldController(holder.mDraweeView.getController())
                    .build();
            holder.mDraweeView.setController(controller);
            
            holder.iv_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialogUtil.showLatLngOptions(mContext, latLng, position,mCallback);
                }
            });

            holder.tv_lat.setText(String.valueOf(latLng.latitude)+"°");
            holder.tv_lng.setText(String.valueOf(latLng.longitude)+"°");
            holder.fl_rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onItemSelected(position,latLng);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_index;
        private SimpleDraweeView mDraweeView;
        private ImageView iv_edit;
        private TextView tv_lat;
        private TextView tv_lng;
        private FrameLayout fl_rootView;


        public ViewHolder(View itemView) {
            super(itemView);
            tv_index= (TextView) itemView.findViewById(R.id.item_save_mapping_data_recycler_item_tv_num);
            mDraweeView= (SimpleDraweeView) itemView.findViewById(R.id.item_save_mapping_data_recycler_item_drawee_view);
            iv_edit= (ImageView) itemView.findViewById(R.id.item_save_mapping_data_recycler_item_iv_edit);
            tv_lat= (TextView) itemView.findViewById(R.id.item_save_mapping_data_recycler_item_tv_lat);
            tv_lng= (TextView) itemView.findViewById(R.id.item_save_mapping_data_recycler_item_tv_lng);
            fl_rootView= (FrameLayout) itemView.findViewById(R.id.item_save_mapping_data_recycler_item_fl_root_view);
        }
    }
}
