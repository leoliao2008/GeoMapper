package com.skycaster.geomapper.receivers;

import android.content.BroadcastReceiver;

/**
 * Created by 廖华凯 on 2017/6/13.
 */

public abstract class MapDownLoadProgressReceiver extends BroadcastReceiver {
    public static final String CITY_NAME="city_name";
    public static final String RATIO="ratio";
    public static final String CITY_ID="city_id";
    public static final String ACTION ="update_off_line_map_down_load_info";
    public static final String STATUS="status";
}
