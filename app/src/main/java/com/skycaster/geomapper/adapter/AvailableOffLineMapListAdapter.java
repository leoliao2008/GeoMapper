package com.skycaster.geomapper.adapter;

import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.OffLineMapAdminActivity;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.base.MyBaseAdapter;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/9.
 */

public class AvailableOffLineMapListAdapter extends MyBaseAdapter<MKOLSearchRecord> {
    private OffLineMapAdminActivity admin;

    public AvailableOffLineMapListAdapter(ArrayList<MKOLSearchRecord> list, OffLineMapAdminActivity context) {
        super(list, context, R.layout.item_available_off_line_map);
        admin =context;
    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, final MKOLSearchRecord item) {
        ViewHolder vh= (ViewHolder) viewHolder;
        vh.tv_cityName.setText(item.cityName);
        vh.tv_mapSize.setText(Formatter.formatFileSize(context, item.size));
        final Button button = vh.btn_downLoad;
        final int cityID = item.cityID;
        switch (admin.getCityStatus(cityID)){
            case MKOLUpdateElement.DOWNLOADING:
                button.setEnabled(false);
                button.setText("下载中");
                break;
            case MKOLUpdateElement.FINISHED:
                MKOLUpdateElement element = admin.getMKOLUpdateElement(cityID);
                if(element.update){
                    button.setEnabled(true);
                    button.setText("下载更新");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            admin.updateCity(cityID);
                        }
                    });
                }else {
                    button.setEnabled(false);
                    button.setText("已下载");
                }
                break;
            case MKOLUpdateElement.SUSPENDED:
                button.setEnabled(true);
                button.setText("继续下载");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        admin.startDownLoad(cityID);
                        button.setEnabled(false);
                        button.setText("下载中");
                    }
                });
                break;
            default:
                button.setEnabled(true);
                button.setText("点击下载");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        admin.startDownLoad(cityID);
                        button.setEnabled(false);
                        button.setText("下载中");
                    }
                });
                break;
        }
    }

    @Override
    protected BaseViewHolder instantiateViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    private class ViewHolder extends BaseViewHolder{
        private TextView tv_cityName;
        private TextView tv_mapSize;
        private Button btn_downLoad;

        public ViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        protected void initViews(View convertView) {
            tv_cityName= (TextView) convertView.findViewById(R.id.item_avail_ol_map_tv_city_name);
            tv_mapSize= (TextView) convertView.findViewById(R.id.item_avail_ol_map_tv_total_size);
            btn_downLoad= (Button) convertView.findViewById(R.id.item_avail_ol_map_btn_down_load);
        }
    }
}
