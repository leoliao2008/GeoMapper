package com.skycaster.geomapper.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {
    private ArrayList<T> list;
    protected Context context;
    private int itemLayoutId;

    protected ArrayList<T> getList(){
        return list;
    }


    public MyBaseAdapter(ArrayList<T> list,Context context,int itemLayoutId) {
        this.list = list;
        this.context=context;
        this.itemLayoutId=itemLayoutId;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHolder viewHolder;
        if(convertView==null){
            convertView=View.inflate(context,itemLayoutId,null);
            viewHolder = instantiateViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (BaseViewHolder) convertView.getTag();
        }
        if(list.size()>0&&position<list.size()){
            populateItemView(viewHolder,list.get(position));
            populateItemView(viewHolder,list.get(position),position);
        }else {
            populateItemView(viewHolder,null);
            populateItemView(viewHolder,null,position);
        }
        return convertView;
    }


    protected abstract void populateItemView(BaseViewHolder viewHolder, T item);

    protected void populateItemView(BaseViewHolder viewHolder, T item,int position){}


    protected abstract BaseViewHolder instantiateViewHolder(View convertView);


}
