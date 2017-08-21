package com.skycaster.geomapper.receivers;

import android.content.BroadcastReceiver;

/**
 * Created by 廖华凯 on 2017/6/14.
 */

public abstract class PortDataReceiver extends BroadcastReceiver {
    public static final String ACTION="action_start_receiving_port_data";
    public static final String DATA="port_data";
}
