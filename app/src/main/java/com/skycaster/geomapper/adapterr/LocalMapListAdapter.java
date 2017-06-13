package com.skycaster.geomapper.adapterr;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.OffLineMapAdminActivity;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.base.MyBaseAdapter;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public class LocalMapListAdapter extends MyBaseAdapter<MKOLUpdateElement> {
    private OffLineMapAdminActivity admin;
    private AlertDialog mAlertDialog;

    public LocalMapListAdapter(ArrayList<MKOLUpdateElement> list, OffLineMapAdminActivity context) {
        super(list, context, R.layout.item_existing_off_line_map);
        admin=context;
    }


    @Override
    protected void populateItemView(BaseViewHolder viewHolder, final MKOLUpdateElement item) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final int cityID = item.cityID;
        holder.tv_cityName.setText(item.cityName);
        holder.tv_totalSize.setText("文件大小："+Formatter.formatFileSize(context,item.size));
        holder.tv_currentProgress.setText("下载进度："+item.ratio+"%");
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete(item.cityName,cityID);
            }
        });
        switch (admin.getCityStatus(cityID)){
            case MKOLUpdateElement.DOWNLOADING:
                holder.btn_upgrade.setText("暂停");
                holder.btn_upgrade.setEnabled(true);
                holder.btn_upgrade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        admin.pauseDownLoad(cityID);
                    }
                });
                break;
            case MKOLUpdateElement.SUSPENDED:
                holder.btn_upgrade.setText("继续");
                holder.btn_upgrade.setEnabled(true);
                holder.btn_upgrade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        admin.startDownLoad(cityID);
                    }
                });
                break;
            case MKOLUpdateElement.FINISHED:
            default:
                holder.btn_upgrade.setText("升级");
                if(item.update){
                    holder.btn_upgrade.setEnabled(true);
                    holder.btn_upgrade.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            admin.updateCity(cityID);
                        }
                    });
                }else {
                    holder.btn_upgrade.setEnabled(false);
                }
                break;
        }
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }

    private void confirmDelete(String cityName, final int cityId){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        mAlertDialog = builder.setTitle("温馨提示")
                .setMessage("您确定要删除" + cityName + "的离线缓存吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        admin.removeCity(cityId);
                        mAlertDialog.dismiss();

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                    }
                })
                .create();
        mAlertDialog.show();
    }


    @Override
    protected BaseViewHolder instantiateViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    private class ViewHolder extends BaseViewHolder{
        private TextView tv_cityName;
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
            tv_currentProgress= (TextView) convertView.findViewById(R.id.item_local_ol_map_tv_current_progress);
            tv_totalSize= (TextView) convertView.findViewById(R.id.item_local_ol_map_tv_total_size);
            btn_upgrade= (Button) convertView.findViewById(R.id.item_local_ol_map_btn_upgrade);
            btn_delete= (Button) convertView.findViewById(R.id.item_local_ol_map_btn_delete);
        }
    }
}
