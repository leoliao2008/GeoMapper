package com.skycaster.geomapper.bean;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * Created by 廖华凯 on 2017/7/11.
 */

public class MyLatLng implements Serializable {
    private double lat;
    private double lng;

    public MyLatLng(LatLng latLng) {
        lat=latLng.latitude;
        lng=latLng.longitude;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
