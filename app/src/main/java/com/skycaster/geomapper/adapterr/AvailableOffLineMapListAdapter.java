package com.skycaster.geomapper.adapterr;

import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.activity.OffLineMapAdminActivity;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.base.MyBaseAdapter;
import com.skycaster.geomapper.bean.AvailableOffLineMap;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/9.
 */

public class AvailableOffLineMapListAdapter extends MyBaseAdapter<AvailableOffLineMap> {

    public AvailableOffLineMapListAdapter(ArrayList<AvailableOffLineMap> list, OffLineMapAdminActivity context) {
        super(list, context, R.layout.item_available_off_line_map);
    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, final AvailableOffLineMap item) {
        ViewHolder vh= (ViewHolder) viewHolder;
        vh.tv_cityName.setText(item.getCityName());
        vh.tv_mapSize.setText(Formatter.formatFileSize(context, item.getServerSize()));
        if(item.isDownLoaded()){
            vh.btn_downLoad.setText("已下载");
            vh.btn_downLoad.setEnabled(false);
        }else {
            vh.btn_downLoad.setText("点击下载");
            vh.btn_downLoad.setEnabled(true);
            vh.btn_downLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((OffLineMapAdminActivity)context).getMkOfflineMap().start(item.getCityId());
                }
            });
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
