package com.skycaster.geomapper.models;

import android.graphics.Color;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextureMapView;
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
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.bean.Vertice;
import com.skycaster.geomapper.customized.SmallMarkerView;
import com.skycaster.geomapper.interfaces.GetGeoInfoListener;

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

    /**
     * 初始化百度地图
     * @param mapView 百度地图
     */
    public void initBaiduMap(TextureMapView mapView){
        BaiduMap baiduMap = mapView.getMap();
        baiduMap.getUiSettings().setCompassEnabled(false);
        baiduMap.setBuildingsEnabled(false);
        baiduMap.setIndoorEnable(false);
    }

    /**
     * 显示当前位置
     * @param map 百度地图
     * @param myLocation 当前位置的百度坐标
     */
    public void updateMyLocation(BaiduMap map, BDLocation myLocation){
        map.setMyLocationEnabled(true);
        MyLocationData myLocationData=new MyLocationData
                .Builder()
                .accuracy(myLocation.getRadius())
                .direction(myLocation.getDirection())
                .latitude(myLocation.getLatitude())
                .longitude(myLocation.getLongitude())
                .build();
        map.setMyLocationData(myLocationData);
        map.setMyLocationConfiguration(myLocationConfig);
    }

    /**
     * 根据坐标跳到百度地图特定位置
     * @param map 百度地图
     * @param lat 经度 必须是转换后的百度坐标的经度，否则位置会有偏移
     * @param lng 纬度 必须是转换后的百度坐标的纬度，否则位置会有偏移
     */
    public synchronized void focusToLocation(BaiduMap map,double lat,double lng){
        MapStatus mapStatus=new MapStatus.Builder().target(new LatLng(lat,lng)).rotate(0).zoom(21).build();
        MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
        map.animateMapStatus(mapStatusUpdate);
    }

    /**
     * 根据坐标跳到百度地图特定位置，可以定义旋转角度及放大级别。
     * @param map 百度地图
     * @param location 百度坐标
     * @param rotateDegree 地图的旋转角度
     * @param zoomLevel 地图的放大级别
     */
    public void focusToLocation(BaiduMap map, BDLocation location, float rotateDegree, float zoomLevel){
        MapStatus mapStatus=new MapStatus.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).rotate(rotateDegree).zoom(zoomLevel).build();
        MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
        map.animateMapStatus(mapStatusUpdate);
    }

    /**
     * 根据坐标跳到百度地图特定位置，旋转角度及放大级别不变。
     * @param map 百度地图
     * @param location 百度坐标
     */
    public void focusToLocation(BaiduMap map, BDLocation location){
        MapStatus mapStatus=new MapStatus.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).build();
        MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
        map.animateMapStatus(mapStatusUpdate);
    }


    /**
     * 把国际坐标转换成百度坐标
     * @param source 标准国际坐标
     * @return 百度坐标
     */
    public BDLocation convertToBaiduCoord(LatLng source){
        converter.from(CoordinateConverter.CoordType.GPS);
        LatLng latLng = converter.coord(source).convert();
        BDLocation bdLocation=new BDLocation();
        bdLocation.setLatitude(latLng.latitude);
        bdLocation.setLongitude(latLng.longitude);
        return bdLocation;
    }

    /**
     * 更新百度地图上的移动轨迹
     * @param mapView 百度地图
     * @param positions 移动轨迹
     * @return 返回相应图层
     * @throws PositionCountsInvalidException 当移动轨迹坐标数量不足两个时会跳出此异常
     */
    public Overlay updateMovingTrace(TextureMapView mapView, ArrayList<LatLng> positions)throws PositionCountsInvalidException {
        int size = positions.size();
        if(size <2){
            throw new PositionCountsInvalidException("地图位置的数量不足2个，不能形成轨迹。");
        }
        //每次更新，只是更新最后两个坐标，前面的轨迹不用改动。
        ArrayList<LatLng> lastTwoLatLngs=new ArrayList<>();
        PolylineOptions polylineOptions=null;
        try {
            LatLng p1 = positions.get(size - 2);
            LatLng p2 = positions.get(size - 1);
            showLog(p1.toString());
            showLog(p2.toString());
            lastTwoLatLngs.add(new LatLng(p1.latitude,p1.longitude));
            lastTwoLatLngs.add(new LatLng(p2.latitude,p2.longitude));
            polylineOptions=new PolylineOptions().color(Color.RED).width(10).points(lastTwoLatLngs).dottedLine(true);
        }catch (ArrayIndexOutOfBoundsException e){
            //避免在摘取坐标过程中，数组的数据被清空，导致抛出异常。
            showLog("ArrayIndexOutOfBoundsException");
            e.printStackTrace();
        }

        return mapView.getMap().addOverlay(polylineOptions);
    }

    /**
     * 根据百度坐标获得附近位置的信息，如街道名称、附近大厦名称等。
     * @param latLng 百度坐标
     * @param listener 由于信息要联网获取，所以要用回调获取信息。
     */
    public void getAdjacentInfoByLatlng(final LatLng latLng, final GetGeoInfoListener listener){
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

    /**
     * 根据轨迹计算测量面积
     * @param list 轨迹
     * @return 测量面积
     */
    public double getPolygonArea(ArrayList<LatLng> list){
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
     * @param a 经度
     * @param b 纬度
     * @return 相对角度
     */
    private double getAngle(LatLng a,LatLng b) {
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

    /**
     * 在百度地图上指定位置添加一个默认的图标
     * @param mapView 百度地图
     * @param lat 经度
     * @param lng 纬度
     * @return 返回该百度图层
     */
    public Overlay addLocationMarkAt(TextureMapView mapView, double lat, double lng) {
        return addLocationMarkAt(mapView,lat,lng,R.drawable.ic_add_loc);
    }

    /**
     * 在百度地图上指定位置添加一个指定的图标
     * @param mapView 百度地图
     * @param lat 经度
     * @param lng 纬度
     * @param srcId 图标的src id
     * @return 返回相应图层
     */
    public Overlay addLocationMarkAt(TextureMapView mapView, double lat, double lng, int srcId) {
        MarkerOptions overlayOptions= new MarkerOptions()
                .position(new LatLng(lat,lng))
                .icon(BitmapDescriptorFactory.fromResource(srcId));
        return mapView.getMap().addOverlay(overlayOptions);
    }


    /**
     * 在百度地图上显示特定的一组轨迹
     * @param baiduMap 百度地图
     * @param routePoints 轨迹
     * @param isBaiduCoord 是否本来就是百度坐标
     * @return 返回相应图层
     */
    public Overlay addHistoryRouteOverlay(BaiduMap baiduMap, ArrayList<LatLng> routePoints,boolean isBaiduCoord) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .color(Color.DKGRAY)
                .dottedLine(true)
                .width(4);
        //如果是百度坐标，直接调用即可
        if(isBaiduCoord){
            polylineOptions.points(routePoints);
        }else {
         //如果是国际坐标，需要转成百度坐标
            polylineOptions.points(convertToBaiduCoords(routePoints));
        }
        return baiduMap.addOverlay(polylineOptions);
    }


    /**
     * 在百度地图上显示特定路径的测量范围、行走路径以及各坐标的位置。
     * @param mapView 百度地图
     * @param mappingRoutes 测绘路径坐标的集合
     * @param isBaiduCoord 是否百度坐标，如果不是，这个函数会把国际坐标转化成百度坐标。
     * @return 一个集合包含了本次绘图所有的图层。
     */
    public ArrayList<Overlay> updateMappingOverLays(TextureMapView mapView,ArrayList<LatLng> mappingRoutes,boolean isBaiduCoord){
        ArrayList<Overlay> overlays=new ArrayList<>();
        ArrayList<LatLng> newRoutes=new ArrayList<>();
        //如果数据源是国际坐标，要转成百度坐标。
        if(isBaiduCoord){
            ArrayList<LatLng> latLngs = convertToBaiduCoords(mappingRoutes);
            newRoutes.addAll(latLngs);
        }else {
            newRoutes.addAll(mappingRoutes);
        }
        int size = newRoutes.size();
        //显示测量范围(蓝色方框)
        OverlayOptions overlayOptions;
        switch (size){
            case 0:
                //坐标数量为0，直接返回null
                return overlays;
            case 1:
                //坐标数量为1，画一个点即可
                //两个相同的坐标连成直线，就是一个点了
                newRoutes.add(newRoutes.get(0));
            case 2:
                //坐标数量为2，画一条线即可
                overlayOptions=new PolylineOptions()
                        .points(newRoutes)
                        .width(4)
                        .color(Color.RED);
                break;
            default:
                //坐标数量为3以上时，画多边形
                overlayOptions=new PolygonOptions()
                        .points(newRoutes)
                        .fillColor(BaseApplication.getContext().getResources().getColor(R.color.colorSkyBlueLight))
                        .stroke(new Stroke(15,15));
                break;
        }
        Overlay area = mapView.getMap().addOverlay(overlayOptions);
        overlays.add(area);
        //显示测量坐标
        for(int i = 0, len = size; i<len; i++){
            String index=String.format("%02d",i+1);
            LatLng latLng = newRoutes.get(i);
            MarkerOptions options=new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromView(new SmallMarkerView(mapView.getContext(),index)))
                    .anchor(0.5f,0.5f)
                    .position(latLng);
            Overlay positionMark = mapView.getMap().addOverlay(options);
            overlays.add(positionMark);
        }
        //显示行走路径
        PolylineOptions options=new PolylineOptions()
                .points(newRoutes)
                .color(Color.RED)
                .width(5)
                .dottedLine(true);
        Overlay route = mapView.getMap().addOverlay(options);
        overlays.add(route);
        //行走路径首尾两个坐标的闭合线
        if(size>2){
            ArrayList<LatLng> list=new ArrayList<>();
            list.add(newRoutes.get(size-1));
            list.add(newRoutes.get(0));
            PolylineOptions opt=new PolylineOptions()
                    .points(list)
                    .color(Color.BLUE)
                    .width(5)
                    .dottedLine(true);
            Overlay closingRoute = mapView.getMap().addOverlay(opt);
            overlays.add(closingRoute);
        }
        return overlays;
    }

    /**
     * 把国际坐标改成百度坐标
     * @param src 国际坐标集合
     * @return 对应百度坐标的集合
     */
    private ArrayList<LatLng> convertToBaiduCoords(ArrayList<LatLng> src) {
        ArrayList<LatLng> dst=new ArrayList<>();
        for(LatLng temp:src){
            BDLocation baiduCoord = convertToBaiduCoord(temp);
            dst.add(new LatLng(baiduCoord.getLatitude(),baiduCoord.getAltitude()));
        }
        return dst;
    }


    public class PositionCountsInvalidException extends Exception{
        public PositionCountsInvalidException(String message) {
            super(message);
        }
    }

    public void setOnMapStatusChangeListener(TextureMapView mapView, BaiduMap.OnMapStatusChangeListener listener){
        mapView.getMap().setOnMapStatusChangeListener(listener);
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }







}
