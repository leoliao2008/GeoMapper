package com.skycaster.geomapper.bean;

/**
 * Created by 廖华凯 on 2017/6/9.
 */

public class OffLineMap {
    protected String cityName;
    protected int cityId;
    protected int serverSize;

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

    public int getServerSize() {
        return serverSize;
    }

    public void setServerSize(int serverSize) {
        this.serverSize = serverSize;
    }
}
