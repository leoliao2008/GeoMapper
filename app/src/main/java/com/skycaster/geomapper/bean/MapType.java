package com.skycaster.geomapper.bean;

/**
 * Created by 廖华凯 on 2017/8/7.
 */

public class MapType {
    private int drawableSrc;
    private String title;
    private int tiandituMapTypeCode;

    public MapType(int drawableSrc, String title, int tiandituMapTypeCode) {
        this.drawableSrc = drawableSrc;
        this.title = title;
        this.tiandituMapTypeCode = tiandituMapTypeCode;
    }

    public int getDrawableSrc() {
        return drawableSrc;
    }

    public String getTitle() {
        return title;
    }

    public int getTiandituMapTypeCode() {
        return tiandituMapTypeCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapType mapType1 = (MapType) o;

        return tiandituMapTypeCode == mapType1.tiandituMapTypeCode;

    }

    @Override
    public int hashCode() {
        return tiandituMapTypeCode;
    }
}
