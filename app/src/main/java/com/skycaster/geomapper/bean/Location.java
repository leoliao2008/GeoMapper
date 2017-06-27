package com.skycaster.geomapper.bean;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/27.
 */

public class Location {
    String title;
    String catalogueName;
    int iconStyle;
    double latitude;
    double longitude;
    double altitude;
    String comments;
    ArrayList<Uri> picList;
    boolean isBaiduCoordinateSystem;
}
