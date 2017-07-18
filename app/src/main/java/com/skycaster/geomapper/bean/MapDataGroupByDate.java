package com.skycaster.geomapper.bean;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/18.
 */

public class MapDataGroupByDate {
    private String date;
    private ArrayList<MappingData>mList=new ArrayList<>();

    public MapDataGroupByDate(String date, ArrayList<MappingData> list) {
        this.date = date;
        mList.addAll(list);
    }

    public boolean addData(MappingData data){
        return mList.add(data);
    }

    public boolean removeData(MappingData data){
        return mList.remove(data);
    }
}
