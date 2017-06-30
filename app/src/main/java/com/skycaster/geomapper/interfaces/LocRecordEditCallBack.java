package com.skycaster.geomapper.interfaces;

import com.skycaster.geomapper.bean.Location;

/**
 * Created by 廖华凯 on 2017/6/30.
 */

public interface LocRecordEditCallBack {
    void onEdit(Location location,int groupPosition);
    void onDelete(Location location,int groupPosition);
}
