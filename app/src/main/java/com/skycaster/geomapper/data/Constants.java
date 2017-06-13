package com.skycaster.geomapper.data;

import android.Manifest;

/**
 * Created by 廖华凯 on 2017/5/15.
 */

public interface Constants {
    //百度鹰眼轨迹服务平台id:141056
    long BAIDU_TRACE_SERVICE_ID=141056;
    String DEVICE_NAME ="TraceDevice_001";
    String[] SYS_PERMISSIONS=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                          Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                                          Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
                                          Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.INTERNET,
                                          Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                                          Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Manifest.permission.WAKE_LOCK};
    String SERIAL_PORT_PATH="serial_port_path";
    String SERIAL_PORT_BAUD_RATE="serial_port_bd_rate";


}
