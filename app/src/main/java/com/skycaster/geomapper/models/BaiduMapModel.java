package com.skycaster.geomapper.models;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.skycaster.geomapper.R;

import java.util.ArrayList;

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

    public void initBaiduMap(TextureMapView mapView){
        BaiduMap baiduMap = mapView.getMap();
        baiduMap.getUiSettings().setCompassEnabled(true);
        baiduMap.setBuildingsEnabled(true);
        baiduMap.setIndoorEnable(true);
    }

    public synchronized void updateMyLocation(BaiduMap map, BDLocation myLocation, @Nullable MyLocationConfiguration config){
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

    public synchronized void focusToLocation(BaiduMap map, BDLocation location, float rotateDegree, float zoomLevel){
        MapStatus mapStatus=new MapStatus.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).rotate(rotateDegree).zoom(zoomLevel).build();
        MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
        map.animateMapStatus(mapStatusUpdate);
    }


    public synchronized BDLocation convertToBaiduCoord(LatLng source){
        converter.from(CoordinateConverter.CoordType.GPS);
        LatLng latLng = converter.coord(source).convert();
        BDLocation bdLocation=new BDLocation();
        bdLocation.setLatitude(latLng.latitude);
        bdLocation.setLongitude(latLng.longitude);
        return bdLocation;
    }

    public Overlay updateMovingTrace(TextureMapView mapView, ArrayList<LatLng> positions)throws PositionCountsInvalidException {
        int size = positions.size();
        if(size <2){
            throw new PositionCountsInvalidException("地图位置的数量不足2个，不能形成轨迹。");
        }
        //每次更新，只是更新最后两个坐标，前面的轨迹不用改动。
        ArrayList<LatLng> lastTwoLatLngs=new ArrayList<>();
        PolylineOptions polylineOptions=null;
        try {
            lastTwoLatLngs.add(positions.get(size-2));
            lastTwoLatLngs.add(positions.get(size-1));
            polylineOptions=new PolylineOptions().color(Color.RED).width(10).points(lastTwoLatLngs);
        }catch (ArrayIndexOutOfBoundsException e){
            //避免在摘取坐标过程中，数组的数据被清空，导致抛出异常。
            e.printStackTrace();
        }
        return mapView.getMap().addOverlay(polylineOptions);
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }

    public class PositionCountsInvalidException extends Exception{
        public PositionCountsInvalidException(String message) {
            super(message);
        }
    }

    public void setOnMapStatusChangeListener(TextureMapView mapView, BaiduMap.OnMapStatusChangeListener listener){
        mapView.getMap().setOnMapStatusChangeListener(listener);
    }






}
