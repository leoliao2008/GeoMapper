package com.skycaster.geomapper.interfaces;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by 廖华凯 on 2017/7/8.
 */

public interface CreateCoordinateCallBack {
    void onCoordinateCreated(LatLng location);
}
