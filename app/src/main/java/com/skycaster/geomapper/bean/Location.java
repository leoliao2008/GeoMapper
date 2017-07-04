package com.skycaster.geomapper.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/27.
 */

public class Location implements Serializable{
    String title="null";
    int iconStyle;
    double latitude;
    double longitude;
    double altitude;
    String comments="null";
    LocationTag tag;
    ArrayList<String> picList;
    boolean isBaiduCoordinateSystem;
    String submitDate="null";

    public Location clone(){
        Location location=new Location();
        location.setTitle(this.title);
        location.setIconStyle(this.iconStyle);
        location.setLatitude(this.latitude);
        location.setLongitude(this.longitude);
        location.setAltitude(this.altitude);
        location.setComments(this.comments);
        location.setTag(this.tag);
        location.setPicList(this.picList);
        location.setBaiduCoordinateSystem(this.isBaiduCoordinateSystem);
        location.setSubmitDate(this.submitDate);
        return location;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        this.picList=picList;
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

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    @Override
    public String toString() {
        return "Location{" +
                "title='" + title + '\'' +
                ", iconStyle=" + iconStyle +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", comments='" + comments + '\'' +
                ", tag=" + tag +
                ", picList=" + picList +
                ", isBaiduCoordinateSystem=" + isBaiduCoordinateSystem +
                ", submitDate='" + submitDate + '\'' +
                '}';
    }

}
