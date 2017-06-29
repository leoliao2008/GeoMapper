package com.skycaster.geomapper.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/27.
 */

public class Location implements Serializable {
    String title;
    String catalogueName;
    int iconStyle;
    double latitude;
    double longitude;
    double altitude;
    String comments;
    LocationTag tag;
    ArrayList<String> picList;
    boolean isBaiduCoordinateSystem;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCatalogueName() {
        return catalogueName;
    }

    public void setCatalogueName(String catalogueName) {
        this.catalogueName = catalogueName;
    }

    public int getIconStyle() {
        return iconStyle;
    }

    public void setIconStyle(int iconStyle) {
        this.iconStyle = iconStyle;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public ArrayList<String> getPicList() {
        return picList;
    }

    public void setPicList(ArrayList<String> picList) {
        this.picList = picList;
    }

    public boolean isBaiduCoordinateSystem() {
        return isBaiduCoordinateSystem;
    }

    public void setBaiduCoordinateSystem(boolean baiduCoordinateSystem) {
        isBaiduCoordinateSystem = baiduCoordinateSystem;
    }

    public LocationTag getTag() {
        return tag;
    }

    public void setTag(LocationTag tag) {
        this.tag = tag;
    }
}
