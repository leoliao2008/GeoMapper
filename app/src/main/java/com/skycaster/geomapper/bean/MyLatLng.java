package com.skycaster.geomapper.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 廖华凯 on 2017/7/11.
 */

public class MyLatLng implements Parcelable {
    private double lat;
    private double lng;
    private int alt;

    public MyLatLng() {

    }

    public MyLatLng(double lat, double lng, int alt) {
        this.lat = lat;
        this.lng = lng;
        this.alt = alt;
    }

    protected MyLatLng(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
        alt = in.readInt();
    }

    public static final Creator<MyLatLng> CREATOR = new Creator<MyLatLng>() {
        @Override
        public MyLatLng createFromParcel(Parcel in) {
            return new MyLatLng(in);
        }

        @Override
        public MyLatLng[] newArray(int size) {
            return new MyLatLng[size];
        }
    };

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getAlt() {
        return alt;
    }

    public void setAlt(int alt) {
        this.alt = alt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeInt(alt);
    }
}
