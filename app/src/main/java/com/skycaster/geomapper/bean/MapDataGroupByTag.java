package com.skycaster.geomapper.bean;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/18.
 */

public class MapDataGroupByTag {
    private Tag mTag;
    private ArrayList<MappingData>mList=new ArrayList<>();

    public MapDataGroupByTag(Tag tag) {
        mTag = tag;
    }

    public Tag getTag() {
        return mTag;
    }

    public ArrayList<MappingData> getList() {
        return mList;
    }

    public boolean addData(MappingData data){
        return mList.add(data);
    }

    public boolean removeData(MappingData data){
        return mList.remove(data);
    }
}
