package com.skycaster.geomapper.util;

import android.support.annotation.Nullable;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.bean.MyLatLng;
import com.skycaster.geomapper.bean.Vertice;
import com.skycaster.geomapper.interfaces.GetGeoInfoListener;

import java.util.ArrayList;

/**
 * 创建者     $Author$
 * 创建时间   2017/5/16 22:04
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MapUtil {
    private static MyLocationConfiguration myLocationConfig =new MyLocationConfiguration(
            MyLocationConfiguration.LocationMode.NORMAL,
            true,
            BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location));

    private static CoordinateConverter converter=new CoordinateConverter();

    /**
     * 将google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标转换成百度坐标
     * @param source 原始坐标
     * @return 百度坐标
     */
    public static synchronized BDLocation convertToBaiduCoord(LatLng source){
        converter.from(CoordinateConverter.CoordType.GPS);
        LatLng latLng = converter.coord(source).convert();
        BDLocation bdLocation=new BDLocation();
        bdLocation.setLatitude(latLng.latitude);
        bdLocation.setLongitude(latLng.longitude);
        return bdLocation;
    }

    public static void initLocationClient(LocationClient locationClient){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span=1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        locationClient.setLocOption(option);
    }

    public static synchronized void updateMyLocation(BaiduMap map,BDLocation myLocation,@Nullable MyLocationConfiguration config){
        map.setMyLocationEnabled(true);
        MyLocationData myLocationData=new MyLocationData
                .Builder()
                .accuracy(myLocation.getRadius())
                .direction(myLocation.getDirection())
                .latitude(myLocation.getLatitude())
                .longitude(myLocation.getLongitude())
                .build();
        map.setMyLocationData(myLocationData);
        if(config==null){
            map.setMyLocationConfiguration(myLocationConfig);
        }else {
            map.setMyLocationConfiguration(config);
        }
    }



    public static synchronized void goToMyLocation(BaiduMap map, BDLocation myLocation,@Nullable MyLocationConfiguration config,double rotateDegree,int zoomLevel){
        updateMyLocation(map,myLocation,config);
        goToLocation(map,myLocation,rotateDegree,zoomLevel);
    }

    public static synchronized void goToLocation(BaiduMap map, double lat,double lng, double rotateDegree, int zoomLevel){
        BDLocation bdLocation=new BDLocation();
        bdLocation.setLatitude(lat);
        bdLocation.setLongitude(lng);
        goToLocation(map,bdLocation,rotateDegree,zoomLevel);
    }

    public static synchronized void goToLocation(BaiduMap map, MyLatLng location, double rotateDegree, int zoomLevel){
        BDLocation bdLocation=new BDLocation();
        bdLocation.setLatitude(location.getLat());
        bdLocation.setLongitude(location.getLng());
        goToLocation(map,bdLocation,rotateDegree,zoomLevel);
    }

    public static synchronized void goToLocation(BaiduMap map,BDLocation location,double rotateDegree,int zoomLevel){
        MapStatus mapStatus=new MapStatus.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).rotate((float) rotateDegree).zoom(zoomLevel).build();
        MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
        map.animateMapStatus(mapStatusUpdate);
    }

    public static synchronized void getAdjacentInfoByLatlng(final LatLng latLng, final GetGeoInfoListener listener){
        final GeoCoder geoCoder = GeoCoder.newInstance();
        GeoCoder.newInstance().setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if(result == null || result.error != SearchResult.ERRORNO.NO_ERROR){
                    listener.onNoResult();
                }else {
                    listener.onGetResult(result);
                }
                geoCoder.destroy();

            }
        });
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));

    }

//    function calcPolygonArea(vertices) {
//        var total = 0;
//
//        for (var i = 0, l = vertices.length; i < l; i++) {
//            var addX = vertices[i].x;
//            var addY = vertices[i == vertices.length - 1 ? 0 : i + 1].y;
//            var subX = vertices[i == vertices.length - 1 ? 0 : i + 1].x;
//            var subY = vertices[i].y;
//
//            total += (addX * addY * 0.5);
//            total -= (subX * subY * 0.5);
//        }
//
//        return Math.abs(total);
//    }

    public static double getPolygonArea(ArrayList<LatLng> list){
        double area=0;
        int size = list.size();
        if(size>2){
            ArrayList<Vertice> vertices=new ArrayList<>();
            vertices.add(new Vertice(0,0));
            for(int i=1;i<size;i++){
                double angle = getAngle(list.get(0), list.get(i));
//                showLog("angle:"+angle);
                double dis= DistanceUtil.getDistance(list.get(0),list.get(i));
                angle=Math.toRadians(angle);
                Vertice vertice = new Vertice(Math.cos(angle) * dis, Math.sin(angle) * dis);
                vertices.add(vertice);
//                showLog("vertice:"+vertice.toString());
            }
//            for(Vertice vertice:vertices){
//                showLog(vertice.toString());
//            }
            for (int i = 0, z = vertices.size(); i < z; i++) {
                double addX = vertices.get(i).getX();
                int j=(i==(vertices.size()-1))?0:(i+1);
                double addY = vertices.get(j).getY();
                double subX = vertices.get(j).getX();
                double subY = vertices.get(i).getY();
                area += (addX * addY * 0.5);
                area -= (subX * subY * 0.5);
            }
        }

        return Math.abs(area);
    }

    /**
     * 根据两个经纬度计算其相对角度
     * @return
     */
    private static double getAngle(LatLng a,LatLng b) {
        double lat_a,lng_a,lat_b,lng_b;
        lat_a=a.latitude;
        lng_a=a.longitude;
        lat_b=b.latitude;
        lng_b=b.longitude;
        double y = Math.sin(lng_b-lng_a) * Math.cos(lat_b);
        double x = Math.cos(lat_a)*Math.sin(lat_b) - Math.sin(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);
        double brng = Math.atan2(y, x);
        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
//        brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise
        brng=brng+90;
        if(brng>=360){
            brng=brng-360;
        }
        return brng;

    }

//    private double angleFromCoordinate(double lat1, double long1, double lat2,
//                                       double long2) {
//
//        double dLon = (long2 - long1);
//
//        double y = Math.sin(dLon) * Math.cos(lat2);
//        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
//                * Math.cos(lat2) * Math.cos(dLon);
//
//        double brng = Math.atan2(y, x);
//
//        brng = Math.toDegrees(brng);
//        brng = (brng + 360) % 360;
//        brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise
//
//        return brng;
//    }





    private static void showLog(String msg){
        LogUtil.showLog("MapUtil",msg);
    }





}
