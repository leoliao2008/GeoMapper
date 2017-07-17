package com.skycaster.geomapper.interfaces;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by 廖华凯 on 2017/7/14.
 */

public interface CoordinateItemEditCallBack {
    void onInsertNewLatlng(int newPosition, LatLng newLatlng);
    void onDeleteLatlng(int position,LatLng latLng);
    void onSaveAs(int position,LatLng latLng);
    void onEdit(int position,LatLng latLng);
    void onItemSelected(int position,LatLng latLng);
    void onLongClickToGetLatlng(int position);
}
