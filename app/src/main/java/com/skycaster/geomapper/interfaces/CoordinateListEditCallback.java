package com.skycaster.geomapper.interfaces;

import com.skycaster.geomapper.bean.Location;

/**
 * Created by 廖华凯 on 2017/7/7.
 */

public interface CoordinateListEditCallback {
    void onRemove(Location location);
    void onEdit(Location location);
    void onInsertNewLocation(int intoPosition);


}
