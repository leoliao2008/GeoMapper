package com.skycaster.geomapper.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;

/**
 * Created by 廖华凯 on 2017/6/8.
 */

public class LocalOffLineMap extends OffLineMap implements Parcelable {
    private int ratio;
    private boolean isNewUpdateAvailable;
    private int status;
    private int localSize;


    public LocalOffLineMap(MKOLUpdateElement element){
        cityName=element.cityName;
        cityId=element.cityID;
        ratio=element.ratio;
        isNewUpdateAvailable=element.update;
        status=element.status;
        serverSize=element.serversize;
        localSize=element.size;
    }

    protected LocalOffLineMap(Parcel in) {
        cityName = in.readString();
        cityId = in.readInt();
        ratio = in.readInt();
        isNewUpdateAvailable = in.readByte() != 0;
        status = in.readInt();
        serverSize = in.readInt();
        localSize = in.readInt();
    }

    public static final Creator<LocalOffLineMap> CREATOR = new Creator<LocalOffLineMap>() {
        @Override
        public LocalOffLineMap createFromParcel(Parcel in) {
            return new LocalOffLineMap(in);
        }

        @Override
        public LocalOffLineMap[] newArray(int size) {
            return new LocalOffLineMap[size];
        }
    };

    public String getStatusDescriptionByCode(){
        switch (status){
            case 0:
                return "UNDEFINED";
            case 1:
                return "DOWNLOADING";
            case 2:
                return "WAITING";
            case 3:
                return "SUSPENDED";
            case 4:
                return "FINISHED";
            case 5:
                return "eOLDSMd5Error";
            case 6:
                return "eOLDSNetError";
            case 7:
                return "eOLDSIOError";
            case 8:
                return "eOLDSWifiError";
            case 9:
                return "eOLDSFormatError";
            case 10:
                return "eOLDSInstalling";
            default:
                return "UNDEFINED";
        }
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public boolean isNewUpdateAvailable() {
        return isNewUpdateAvailable;
    }

    public void setNewUpdateAvailable(boolean newUpdateAvailable) {
        isNewUpdateAvailable = newUpdateAvailable;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public int getServerSize() {
        return serverSize;
    }

    public void setServerSize(int serverSize) {
        this.serverSize = serverSize;
    }

    public int getLocalSize() {
        return localSize;
    }

    public void setLocalSize(int localSize) {
        this.localSize = localSize;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cityName);
        dest.writeInt(cityId);
        dest.writeInt(ratio);
        dest.writeByte((byte) (isNewUpdateAvailable ? 1 : 0));
        dest.writeInt(status);
        dest.writeInt(serverSize);
        dest.writeInt(localSize);
    }
}
