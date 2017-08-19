package com.skycaster.geomapper.data;

import android.Manifest;

/**
 * Created by 廖华凯 on 2017/5/15.
 */

public interface StaticData {
    //百度鹰眼轨迹服务平台id:141056
    long BAIDU_TRACE_SERVICE_ID=141056;
    String DEVICE_NAME ="TraceDevice_001";
    String[] SYS_PERMISSIONS=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                          Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                                          Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
                                          Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.INTERNET,
                                          Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                                          Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Manifest.permission.WAKE_LOCK,
                                          Manifest.permission.CAMERA,Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN};
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

    int REQUEST_CODE_BLUE_TOOTH_PERMISSIONS=1997;
    int REQUEST_CODE_ENABLE_BLUETOOTH = 1998;
    String UUID="00001101-0000-1000-8000-00805F9B34FB";
    int REQUEST_CODE_DISCOVERABLE =1999;
    String SEVER_NAME="SkyCasterBluetoothServer";
    String TEST_LINE_1="$GPGGA,061923.00,2234.22210054,N,11356.24785338,E,5,05,2.8,48.923,M,-3.475,M,0.0,0693*67";
    String TEST_LINE_2="$GPGGA,061924.00,2234.22226763,N,11356.24775916,E,4,05,2.8,49.498,M,-3.475,M,0.0,0693*63";
    String TEST_LINE_3="$GPGGA,061925.00,2234.22237929,N,11356.24770956,E,5,05,2.8,49.872,M,-3.475,M,0.0,0693*6B";
    String [] TEST_LINES=new String[]{TEST_LINE_1,TEST_LINE_2,TEST_LINE_3};
    int RESULT_CODE_REQUEST_DISCOVERABLE =300;

    String SP_NAME="Config";
    String MAP_TYPE_CODE="MAP_TYPE_CODE";
    int BLUETOOTH_SERVICE_FOREGROUND_ID =2000;
    String ACTION_RECEIVE_BLUETOOTH_DATA="ACTION_RECEIVE_BLUETOOTH_DATA";
    String EXTRA_BLUETOOTH_DATA="EXTRA_BLUETOOTH_DATA";
    String EXTRA_BLUETOOTH_CLIENT_MODEL_CALLBACK = "EXTRA_BLUETOOTH_CLIENT_MODEL_CALLBACK";
    String EXTRA_BLUETOOTH_DEVICE = "EXTRA_BLUETOOTH_DEVICE";
    String EXTRA_BLUETOOTH_STATE = "EXTRA_BLUETOOTH_STATE";
    int EXTRA_BLUETOOTH_STATE_SOCKET_FAIL_TO_CONNECT = 1;
    int EXTRA_BLUETOOTH_STATE_DATA_SUCCESS = 2;
    int EXTRA_BLUETOOTH_STATE_DISCONNECT = 3;
    String ACTION_STOP_BLUETOOTH_SERVICE = "ACTION_STOP_BLUETOOTH_SERVICE";
    String NAVI_MODE ="OpenTrackingMode";

    byte[] STOP_SENDING_MESSAGES =new byte[]{
            0x02,0x00,0x64,0x0D,0x00,0x00,0x00,0x03,0x00,0x01,0x00,0x07,0x04, (byte) 0xFF,0x00,0x00,0x00,0x7F,0x03
    };//停止发送传统信息
    byte[] SEND_GPGGA_MESSAGE_ONLY=new byte[]{
            0x02,0x00,0x64,0x0D,0x00,0x00,0x00,0x03,0x00,0x01,0x00,0x07,0x04,0x06,0x00,0x03,0x00, (byte) 0x89,0x03
    };//只发送gpgga信息
}
