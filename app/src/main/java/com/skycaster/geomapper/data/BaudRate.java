package com.skycaster.geomapper.data;

/**
 * Created by 廖华凯 on 2017/6/13.
 */

public enum BaudRate {
    BD_9600,BD_19200,BD_38400,BD_57600,BD_115200;

    @Override
    public String toString() {
        return super.toString().split("_")[1];
    }

    public int toInt() {
        return Integer.valueOf(toString());}
}
