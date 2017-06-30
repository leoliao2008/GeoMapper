package com.skycaster.geomapper.bean;

import java.io.Serializable;

/**
 * Created by 廖华凯 on 2017/6/28.
 */

public class LocationTag implements Serializable{
    private String tagName;
    private int id;

    public LocationTag(String tagName, int id) {
        this.tagName = tagName;
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationTag that = (LocationTag) o;

        if (id != that.id) return false;
        return tagName != null ? tagName.equals(that.tagName) : that.tagName == null;

    }

    @Override
    public int hashCode() {
        int result = tagName != null ? tagName.hashCode() : 0;
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "LocationTag{" +
                "tagName='" + tagName + '\'' +
                ", id=" + id +
                '}';
    }
}
