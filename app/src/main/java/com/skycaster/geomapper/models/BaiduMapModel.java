package com.skycaster.geomapper.models;

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
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.bean.MyLatLng;
import com.skycaster.geomapper.interfaces.GetGeoInfoListener;
import com.skycaster.geomapper.util.LogUtil;

/**
 * Created by 廖华凯 on 2017/8/15.
 */

public class BaiduMapModel {

    private  MyLocationConfiguration myLocationConfig;
    private CoordinateConverter converter;

    public BaiduMapModel() {
        myLocationConfig =new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL,
                true,
                BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location));
        converter=new CoordinateConverter();

    }

    /**
     * 将google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标转换成百度坐标
     * @param source 原始坐标
     * @return 百度坐标
     */
    public synchronized BDLocation convertToBaiduCoord(LatLng source){
        converter.from(CoordinateConverter.CoordType.GPS);
        LatLng latLng = converter.coord(source).convert();
        BDLocation bdLocation=new BDLocation();
        bdLocation.setLatitude(latLng.latitude);
        bdLocation.setLongitude(latLng.longitude);
        return bdLocation;
    }

    public void initLocationClient(LocationClient locationClient){
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

    public synchronized void updateMyLocation(BaiduMap map, BDLocation myLocation, @Nullable MyLocationConfiguration config){
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

    public synchronized void goToMyLocation(BaiduMap map, BDLocation myLocation,@Nullable MyLocationConfiguration config,double rotateDegree,int zoomLevel){
        updateMyLocation(map,myLocation,config);
        goToLocation(map,myLocation,rotateDegree,zoomLevel);
    }

    public synchronized void goToLocation(BaiduMap map, double lat,double lng, double rotateDegree, int zoomLevel){
        BDLocation bdLocation=new BDLocation();
        bdLocation.setLatitude(lat);
        bdLocation.setLongitude(lng);
        goToLocation(map,bdLocation,rotateDegree,zoomLevel);
    }

    public synchronized void goToLocation(BaiduMap map, MyLatLng location, double rotateDegree, int zoomLevel){
        BDLocation bdLocation=new BDLocation();
        bdLocation.setLatitude(location.getLat());
        bdLocation.setLongitude(location.getLng());
        goToLocation(map,bdLocation,rotateDegree,zoomLevel);
    }

    public synchronized void goToLocation(BaiduMap map,BDLocation location,double rotateDegree,int zoomLevel){
        MapStatus mapStatus=new MapStatus.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).rotate((float) rotateDegree).zoom(zoomLevel).build();
        MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
        map.animateMapStatus(mapStatusUpdate);
    }

    public synchronized void getAdjacentInfoByLatlng(final LatLng latLng, final GetGeoInfoListener listener){
        final GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
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

    private void showLog(String msg){
        LogUtil.showLog(getClass().getSimpleName(),msg);
    }






}