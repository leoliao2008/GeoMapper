package com.skycaster.geomapper.customized;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.bean.Vertice;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/11/4.
 */

public class MappingResultPanel extends FrameLayout {
    private TextView mTvPathLength;
    private TextView mTvPerimeter;
    private TextView mTvAcreage;
    private double mDistance;
    private double mArea;

    public MappingResultPanel(Context context) {
        this(context, null);
    }

    public MappingResultPanel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MappingResultPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LinearLayout rootView = (LinearLayout) View.inflate(context, R.layout.widget_mapping_reads, null);
        mTvPathLength= (TextView) rootView.findViewById(R.id.activity_mapping_tv_path_length);
        mTvPerimeter= (TextView) rootView.findViewById(R.id.activity_mapping_tv_perimeter);
        mTvAcreage= (TextView) rootView.findViewById(R.id.activity_mapping_tv_acreage);
        addView(rootView);
    }

    public void restoreToDefault(){
        mTvPathLength.setText("0");
        mTvPerimeter.setText("0");
        mTvAcreage.setText("0");
    }

    public void updateMappingResult(final ArrayList<LatLng> route){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = route.size();
                if(size<1){
                    return;
                }
                mDistance = 0;
                if(size>1){
                    for(int i = 1; i<size; i++){
                        mDistance += DistanceUtil.getDistance(route.get(i-1),route.get(i));
                    }
                }
                BaseApplication.post(new Runnable() {
                    @Override
                    public void run() {
                        mTvPathLength.setText(String.format("%.02f", mDistance));
                    }
                });
                if(size>2){
                    mDistance +=DistanceUtil.getDistance(route.get(size-1),route.get(0));
                    BaseApplication.post(new Runnable() {
                        @Override
                        public void run() {
                            mTvPerimeter.setText(String.format("%.02f", mDistance));
                        }
                    });
                }else {
                    BaseApplication.post(new Runnable() {
                        @Override
                        public void run() {
                            mTvPerimeter.setText(String.format("%.02f",0.f));
                        }
                    });

                }
                mArea = getPolygonArea(route);
                BaseApplication.post(new Runnable() {
                    @Override
                    public void run() {
                        mTvAcreage.setText(String.format("%.02f", mArea));
                    }
                });
            }
        }).start();
    }

    private double getPolygonArea(ArrayList<LatLng> list){
        double area=0;
        int size = list.size();
        if(size>2){
            ArrayList<Vertice> vertices=new ArrayList<>();
            vertices.add(new Vertice(0,0));
            for(int i=1;i<size;i++){
                double angle = getAngle(list.get(0), list.get(i));
                double dis= DistanceUtil.getDistance(list.get(0),list.get(i));
                angle=Math.toRadians(angle);
                Vertice vertice = new Vertice(Math.cos(angle) * dis, Math.sin(angle) * dis);
                vertices.add(vertice);
            }
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
        brng=brng+90;
        if(brng>=360){
            brng=brng-360;
        }
        return brng;
    }
}
