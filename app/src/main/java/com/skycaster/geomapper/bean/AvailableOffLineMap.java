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
    private boolean isDownLoaded;

    public AvailableOffLineMap(MKOLSearchRecord record) {
        cityName=record.cityName;
        cityId=record.cityID;
        serverSize=record.size;
        isDownLoaded=false;
    }

    protected AvailableOffLineMap(Parcel in) {
        cityName=in.readString();
        cityId=in.readInt();
        serverSize=in.readInt();
        progress=in.readInt();
        status=in.readInt();
        isDownLoaded=in.readByte()==1;
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

    public boolean isDownLoaded() {
        return isDownLoaded;
    }

    public void setDownLoaded(boolean downLoaded) {
        isDownLoaded = downLoaded;
    }

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
        dest.writeByte((byte) (isDownLoaded?1:0));
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
