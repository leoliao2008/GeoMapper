package com.skycaster.geomapper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.viewHolder.FileBrowserViewHolder;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/8/22.
 */

public class FileBrowserAdapter extends RecyclerView.Adapter<FileBrowserViewHolder> {
    private ArrayList<File> list;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private ArrayList<File> mStack=new ArrayList<>();

    public FileBrowserAdapter(ArrayList<File> list, Context context) {
        this.list = list;
        mContext = context;
    }

    @Override
    public FileBrowserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView=View.inflate(mContext, R.layout.item_file_browser,null);
        return new FileBrowserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FileBrowserViewHolder holder, final int position) {
        final File file = list.get(position);
        if(file.isDirectory()){
            File[] listFiles = file.listFiles();
            if(listFiles !=null&&listFiles.length>0){
                holder.getIv_icon().setImageResource(R.drawable.selector_ic_dir_solid);
            }else {
                holder.getIv_icon().setImageResource(R.drawable.selector_ic_dir_empty);
            }
        }else {
            holder.getIv_icon().setImageResource(R.drawable.selector_ic_file);
        }
        holder.getTv_name().setText(file.getName());
        holder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(file.isDirectory()){
                    File parentFile = file.getParentFile();
                    if(parentFile!=null){
                        mStack.add(parentFile);
                    }
                }
                if(mOnItemClickListener !=null){
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });

        holder.getRootView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mOnItemClickListener!=null){
                    mOnItemClickListener.onItemLongClick(position);
                    return true;
                }
                return false;
            }
        });
    }

    public void changeDir(File dir){
        if(dir.isDirectory()){
            File[] files = dir.listFiles();
            list.clear();
            for(File temp:files){
                list.add(temp);
            }
            notifyDataSetChanged();
        }
    }

    public void back(){
        int size = mStack.size();
        if(size>0){
            changeDir(mStack.get(size-1));
            mStack.remove(size-1);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
