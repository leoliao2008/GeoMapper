package com.skycaster.geomapper.adapterr;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.base.MyBaseAdapter;
import com.skycaster.geomapper.bean.AvailableOffLineMap;
import com.skycaster.geomapper.util.ToastUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/9.
 */

public class AvailableOffLineMapListAdapter extends MyBaseAdapter<AvailableOffLineMap> {
    public AvailableOffLineMapListAdapter(ArrayList<AvailableOffLineMap> list, Context context) {
        super(list, context, R.layout.item_available_off_line_map);
    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, AvailableOffLineMap item) {
        ViewHolder vh= (ViewHolder) viewHolder;
        vh.tv_cityName.setText(item.getCityName());
        vh.tv_mapSize.setText(Formatter.formatFileSize(context, item.getServerSize()));
        vh.btn_downLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast("点击了下载。");
            }
        });

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
