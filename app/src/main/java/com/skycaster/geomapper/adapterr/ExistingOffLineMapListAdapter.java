package com.skycaster.geomapper.adapterr;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.base.MyBaseAdapter;
import com.skycaster.geomapper.bean.ExistingOffLineMap;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public class ExistingOffLineMapListAdapter extends MyBaseAdapter<ExistingOffLineMap> {


    public ExistingOffLineMapListAdapter(ArrayList<ExistingOffLineMap> list, Context context) {
        super(list, context, R.layout.item_existing_off_line_map);
    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, ExistingOffLineMap item) {
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.tv_cityName.setText(item.getCityName());
        if(item.isNewUpdateAvailable()){
            holder.tv_isUpgradeAvailable.setVisibility(View.VISIBLE);
        }else {
            holder.tv_isUpgradeAvailable.setVisibility(View.INVISIBLE);
        }
        holder.tv_totalSize.setText(item.getServerSize()+"M");
        holder.tv_currentProgress.setText(item.getRatio()+"%");
        holder.btn_upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("点击了升级。");
            }
        });
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog("点击了删除。");
            }
        });
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }


    @Override
    protected BaseViewHolder instantiateViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    private class ViewHolder extends BaseViewHolder{
        private TextView tv_cityName;
        private TextView tv_isUpgradeAvailable;
        private TextView tv_currentProgress;
        private TextView tv_totalSize;
        private Button btn_upgrade;
        private Button btn_delete;

        private ViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        protected void initViews(View convertView) {
            tv_cityName= (TextView) convertView.findViewById(R.id.item_local_ol_map_tv_city_name);
            tv_isUpgradeAvailable= (TextView) convertView.findViewById(R.id.item_local_ol_map_tv_is_update_available);
            tv_currentProgress= (TextView) convertView.findViewById(R.id.item_local_ol_map_tv_current_progress);
            tv_totalSize= (TextView) convertView.findViewById(R.id.item_local_ol_map_tv_total_size);
            btn_upgrade= (Button) convertView.findViewById(R.id.item_local_ol_map_btn_upgrade);
            btn_delete= (Button) convertView.findViewById(R.id.item_local_ol_map_btn_delete);
        }
    }
}
