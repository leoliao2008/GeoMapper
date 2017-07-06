package com.skycaster.geomapper.interfaces;

import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

/**
 * Created by 廖华凯 on 2017/7/6.
 */

public interface GetGeoInfoListener {
    void onGetResult(ReverseGeoCodeResult result);
    void onNoResult();
}
