package com.skycaster.geomapper.interfaces;

import com.skycaster.geomapper.bean.Location;

/**
 * Created by 廖华凯 on 2017/6/30.
 */

public interface LocRecordEditCallBack {
    void onEdit(Location location);
    void onDelete(Location location);
    void onViewDetail(Location location);
}
