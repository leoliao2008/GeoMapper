package com.skycaster.geomapper.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/7/11.
 */

public class MappingData implements Parcelable {
    private String title;
    private SimpleDateFormat mDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private ArrayList<MyLatLng> mLatLngs;
    private String comment;
    private String address;
    private String adjacentLoc;
    private double pathLength;
    private double perimeter;
    private double area;
    private long id;
    private String date;
    private int tagID;
    private String tagName;

    public MappingData(
            String title,
            ArrayList<MyLatLng> latLngs,
            String comment,
            String address,
            String adjacentLoc,
            double pathLength,
            double perimeter,
            double area,
            int tagID,
            String tagName) {
        Date date = new Date();
        this.title = title;
        mLatLngs = latLngs;
        id= date.getTime();
        this.comment = comment;
        this.address = address;
        this.adjacentLoc=adjacentLoc;
        this.pathLength=pathLength;
        this.perimeter = perimeter;
        this.area = area;
        this.date=mDateFormat.format(date);
        this.tagID=tagID;
        this.tagName=tagName;
    }

    public MappingData(
            String title,
            ArrayList<MyLatLng> latLngs,
            String comment,
            String address,
            String adjacentLoc,
            double pathLength,
            double perimeter,
            double area,
            long id,
            String date,
            int tagID,
            String tagName) {
        this.title = title;
        mLatLngs = latLngs;
        this.comment = comment;
        this.address = address;
        this.adjacentLoc = adjacentLoc;
        this.pathLength = pathLength;
        this.perimeter = perimeter;
        this.area = area;
        this.id = id;
        this.date = date;
        this.tagName=tagName;
        this.tagID=tagID;
    }


    protected MappingData(Parcel in) {
        title = in.readString();
        mLatLngs = in.createTypedArrayList(MyLatLng.CREATOR);
        comment = in.readString();
        address = in.readString();
        adjacentLoc = in.readString();
        pathLength = in.readDouble();
        perimeter = in.readDouble();
        area = in.readDouble();
        id = in.readLong();
        date = in.readString();
        tagID = in.readInt();
        tagName = in.readString();
    }

    public static final Creator<MappingData> CREATOR = new Creator<MappingData>() {
        @Override
        public MappingData createFromParcel(Parcel in) {
            return new MappingData(in);
        }

        @Override
        public MappingData[] newArray(int size) {
            return new MappingData[size];
        }
    };

    public String getDate() {
        return date;
    }

    public String getAdjacentLoc() {
        return adjacentLoc;
    }

    public void setAdjacentLoc(String adjacentLoc) {
        this.adjacentLoc = adjacentLoc;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLatLngs(ArrayList<MyLatLng> latLngs) {
        mLatLngs = latLngs;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPerimeter(long perimeter) {
        this.perimeter = perimeter;
    }

    public void setArea(long area) {
        this.area = area;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<MyLatLng> getLatLngs() {
        return mLatLngs;
    }

    public String getComment() {
        return comment;
    }

    public String getAddress() {
        return address;
    }

    public double getPerimeter() {
        return perimeter;
    }

    public double getArea() {
        return area;
    }

    public long getId() {
        return id;
    }

    public double getPathLength() {
        return pathLength;
    }

    public void setPathLength(double pathLength) {
        this.pathLength = pathLength;
    }

    public int getTagID() {
        return tagID;
    }

    public void setTagID(int tagID) {
        this.tagID = tagID;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MappingData data = (MappingData) o;

        if (id != data.id) return false;
        return date.equals(data.date);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + date.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedList(mLatLngs);
        dest.writeString(comment);
        dest.writeString(address);
        dest.writeString(adjacentLoc);
        dest.writeDouble(pathLength);
        dest.writeDouble(perimeter);
        dest.writeDouble(area);
        dest.writeLong(id);
        dest.writeString(date);
        dest.writeInt(tagID);
        dest.writeString(tagName);
    }
}
