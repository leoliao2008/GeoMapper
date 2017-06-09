package com.skycaster.geomapper.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;

/**
 * Created by 廖华凯 on 2017/6/9.
 */

public class AvailableOffLineMap extends OffLineMap implements Parcelable {
    public static final int STATUS_DOWN_LOADING=1;
    public static final int STATUS_PAUSE=2;
    public static final int STATUS_DEFAULT=0;
    private int status;
    private int progress;

    public AvailableOffLineMap(MKOLSearchRecord record) {
        cityName=record.cityName;
        cityId=record.cityID;
        serverSize=record.size;
    }

    protected AvailableOffLineMap(Parcel in) {
        cityName=in.readString();
        cityId=in.readInt();
        serverSize=in.readInt();
        progress=in.readInt();
        status=in.readInt();
    }

    public static final Creator<AvailableOffLineMap> CREATOR = new Creator<AvailableOffLineMap>() {
        @Override
        public AvailableOffLineMap createFromParcel(Parcel in) {
            return new AvailableOffLineMap(in);
        }

        @Override
        public AvailableOffLineMap[] newArray(int size) {
            return new AvailableOffLineMap[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cityName);
        dest.writeInt(cityId);
        dest.writeInt(serverSize);
        dest.writeInt(progress);
        dest.writeInt(status);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
