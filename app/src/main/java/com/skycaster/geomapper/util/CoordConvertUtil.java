package com.skycaster.geomapper.util;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

/**
 * Created by 廖华凯 on 2017/5/15.
 */

public class CoordConvertUtil {
    private static CoordinateConverter converter=new CoordinateConverter();

    /**
     * 将google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标转换成百度坐标
     * @param source 原始坐标
     * @return 百度坐标
     */
    public static synchronized LatLng toBaiduCoord(LatLng source){
        converter.from(CoordinateConverter.CoordType.COMMON);
        return converter.coord(source).convert();
    }
}
