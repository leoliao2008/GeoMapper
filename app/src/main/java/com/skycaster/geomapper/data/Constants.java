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
                                          Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Manifest.permission.WAKE_LOCK,
                                          Manifest.permission.CAMERA};
    String SERIAL_PORT_PATH="serial_port_path";
    String SERIAL_PORT_BAUD_RATE="serial_port_bd_rate";
    String LOCATION_INFO="location_info";
    int RESULT_CODE_MODIFICATION_SUCCESS =9413;
    String BAIDU_SECURITY_CODE="FC:5E:65:65:F3:97:6C:4A:DF:1E:8E:A0:B8:2B:C9:EC:A5:2D:81:64;com.skycaster.geomapper";
    String BAIDU_AK="l9gPMORpg6orolrZGEvES5iy1FrDN5wd";
    String LAYOUT_PATTERN="MappingDataLayoutPattern";
    String EXTRA_COORDINATES="coordinates";
    String MAPPING_DATA_SAVED = "mapping_data_which_has_just_been_saved";
    String GROUP_POSITION = "group_position";
    String CHILD_POSITION ="child_position";
    String MAPPING_DATA_SOURCE = "mapping_data_which_is_to_be_edit";

    String IS_TAG_MODIFIED = "has_the_tag_for_mapping_data_been_modified";
}
