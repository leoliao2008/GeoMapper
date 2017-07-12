package com.skycaster.geomapper.util;

import com.skycaster.geomapper.data.Constants;

/**
 * Created by 廖华凯 on 2017/7/12.
 */

public class NetUtil {

    public static String generatePanoramaUrl(double lat,double lng,int width,int height){
        StringBuilder sb=new StringBuilder();
        sb.append("http://api.map.baidu.com/panorama/v2?mcode=")
                .append(Constants.BAIDU_SECURITY_CODE)
                .append("&ak=")
                .append(Constants.BAIDU_AK)
                .append("&width=")
                .append(width)
                .append("&height=")
                .append(height)
                .append("&location=")
                .append(lng)
                .append(",")
                .append(lat)
                .append("&fov=180");
        return sb.toString();
    }
}
