package com.skycaster.geomapper.bean;

import com.baidu.mapapi.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/7/11.
 */

public class MappingData {
    private String title;
    private SimpleDateFormat mDateFormat=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.CHINA);
    private ArrayList<LatLng> mLatLngs;
    private String date;
    private String comment;
    private String address;
    private long perimeter;
    private long area;
    private int iconType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public ArrayList<LatLng> getLatLngs() {
        return mLatLngs;
    }

    public void setLatLngs(ArrayList<LatLng> latLngs) {
        mLatLngs = latLngs;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getPerimeter() {
        return perimeter;
    }

    public void setPerimeter(long perimeter) {
        this.perimeter = perimeter;
    }

    public long getArea() {
        return area;
    }

    public void setArea(long area) {
        this.area = area;
    }

    public int getIconType() {
        return iconType;
    }

    public void setIconType(int iconType) {
        this.iconType = iconType;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getID() {
        Date date=null;
        try {
            date = mDateFormat.parse(this.date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date!=null){
            return date.getTime();
        }else {
            return -1;
        }
    }
}
