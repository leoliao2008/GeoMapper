package com.skycaster.geomapper.bean;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/30.
 */

public class LocRecordGroupItem {
    private Tag mTag;
    private ArrayList<Location> mLocations=new ArrayList<>();

    public LocRecordGroupItem(Tag tag) {
        mTag = tag;
    }

    public void addLocation(Location location){
        mLocations.add(location);
    }

    public Tag getTag() {
        return mTag;
    }

    public void setTag(Tag tag) {
        mTag = tag;
    }

    public ArrayList<Location> getLocations() {
        return mLocations;
    }

    public void setLocations(ArrayList<Location> locations) {
        mLocations.addAll(locations);
    }
}
