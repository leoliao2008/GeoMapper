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
}
