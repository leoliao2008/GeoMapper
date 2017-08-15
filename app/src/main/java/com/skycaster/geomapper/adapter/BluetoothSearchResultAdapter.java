package com.skycaster.geomapper.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseViewHolder;
import com.skycaster.geomapper.base.MyBaseAdapter;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/8/15.
 */

public class BluetoothSearchResultAdapter extends MyBaseAdapter<BluetoothDevice> {

    public BluetoothSearchResultAdapter(ArrayList<BluetoothDevice> list, Context context) {
        super(list, context, R.layout.item_bluetooth_discover_result);
    }

    @Override
    protected void populateItemView(BaseViewHolder viewHolder, BluetoothDevice item) {
        ViewHolder vh= (ViewHolder) viewHolder;
        vh.tv_deviceName.setText(item.getName());
        vh.tv_deviceAddress.setText("Mac: "+item.getAddress());
    }

    @Override
    protected BaseViewHolder instantiateViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    private class ViewHolder extends BaseViewHolder{
        private TextView tv_deviceName;
        private TextView tv_deviceAddress;

        public ViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        protected void initViews(View convertView) {
            tv_deviceAddress= (TextView) findViewById(R.id.item_blue_tooth_discover_result_tv_device_address);
            tv_deviceName= (TextView) findViewById(R.id.item_blue_tooth_discover_result_tv_device_name);

        }
    }
}
