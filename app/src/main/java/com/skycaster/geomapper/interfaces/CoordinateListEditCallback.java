package com.skycaster.geomapper.interfaces;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by 廖华凯 on 2017/7/7.
 */

public interface CoordinateListEditCallback {
    void onRemove(LatLng latLng);
    void onEdit(LatLng latLng, int position);
    void onInsertNewLocation(int intoPosition);
    void onSave(LatLng latLng);


}
