package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.skycaster.geomapper.R;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/10/28.
 */

public class RawDataRecyclerViewAdapter extends RecyclerView.Adapter<RawDataRecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> mList;
    private Context mContext;
    private float mTextSize;

    public RawDataRecyclerViewAdapter(ArrayList<String> list, Context context) {
        mList = list;
        mContext = context;
        DisplayMetrics metrics=new DisplayMetrics();
        WindowManager manager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        mTextSize = mContext.getResources().getDimension(R.dimen.sp_24) / metrics.scaledDensity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(mContext,android.R.layout.simple_list_item_1,null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getTextView().setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView= (TextView) itemView;
            mTextView.setTextSize(mTextSize);
        }

        public TextView getTextView() {
            return mTextView;
        }
    }
}
