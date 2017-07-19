package com.skycaster.geomapper.bean;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/18.
 */

public class MapDataGroupByDate {
    private String date;
    private ArrayList<MappingData>mList=new ArrayList<>();

    public MapDataGroupByDate(String date) {
        this.date=date;
    }

    public boolean addData(MappingData data){
        return mList.add(data);
    }

    public boolean removeData(MappingData data){
        return mList.remove(data);
    }

    public MappingData removeData(int childIndex){
        return mList.remove(childIndex);
    }

    public void addData(int childIndex,MappingData data){
        mList.add(childIndex,data);
    }

    public ArrayList<MappingData> getList() {
        return mList;
    }

    public String getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapDataGroupByDate that = (MapDataGroupByDate) o;

        return date != null ? date.equals(that.date) : that.date == null;

    }

    @Override
    public int hashCode() {
        return date != null ? date.hashCode() : 0;
    }
}
