package com.skycaster.geomapper.bean;

import com.baidu.mapapi.map.BaiduMap;
import com.skycaster.geomapper.R;

/**
 * Created by 廖华凯 on 2017/8/7.地图类型，一共三种，分别对应不同的标题和图案，都在这里封装好了，配合百度地图SDK使用。
 */

public class MapType {
    private int drawableSrc;
    private String title;
    private int mapTypeCode;

    public MapType(int mapTypeCode) {
        this.mapTypeCode = mapTypeCode;
        switch (mapTypeCode){
            case BaiduMap.MAP_TYPE_SATELLITE:
                this.drawableSrc= R.drawable.ic_map_type_satellite;
                this.title="卫星图";
                break;
            case BaiduMap.MAP_TYPE_NONE:
                this.drawableSrc= R.drawable.ic_map_type_terrain;
                this.title="空地图";
                break;
            case BaiduMap.MAP_TYPE_NORMAL:
            default:
                this.drawableSrc=R.drawable.ic_map_type_vector;
                this.title="矢量图";
                break;
        }
    }

    public int getDrawableSrc() {
        return drawableSrc;
    }

    public String getTitle() {
        return title;
    }

    public int getMapTypeCode() {
        return mapTypeCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapType mapType1 = (MapType) o;

        return mapTypeCode == mapType1.mapTypeCode;

    }

    @Override
    public int hashCode() {
        return mapTypeCode;
    }
}
