package com.skycaster.geomapper.bean;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/30.
 */

public class LocRecordGroupItem {
    private LocationTag mLocationTag;
    private ArrayList<Location> mLocations=new ArrayList<>();

    public LocRecordGroupItem(LocationTag locationTag) {
        mLocationTag = locationTag;
    }

    public void addLocation(Location location){
        mLocations.add(location);
    }

    public LocationTag getLocationTag() {
        return mLocationTag;
    }

    public void setLocationTag(LocationTag locationTag) {
        mLocationTag = locationTag;
    }

    public ArrayList<Location> getLocations() {
        return mLocations;
    }

    public void setLocations(ArrayList<Location> locations) {
        mLocations.addAll(locations);
    }
}
