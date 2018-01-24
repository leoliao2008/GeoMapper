package com.skycaster.geomapper.models;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 廖华凯 on 2017/10/21.
 */

public class GPIOModel {

    private static final String GPIO_CONTROL_POW = "/sys/class/gpio/gpio205/value"; //控制CDRadio芯片的电源
    private static final String GPIO_CONTROL_DIRECTION ="/sys/class/gpio/gpio216/value";//控制CDRadio芯片的串口连接方向（通向手机主板电路或北斗模块电路）

    /**
     * CDRadio芯片上电
     * @throws IOException
     */
    public void turnOnCdRadio() throws IOException {
        writeFile(GPIO_CONTROL_POW,"1".getBytes());
    }

    /**
     * CDRadio芯片断电
     * @throws IOException
     */
    public void turnOffCdRadio() throws IOException {
        writeFile(GPIO_CONTROL_POW,"0".getBytes());
    }

    /**
     * 连接CDRadio芯片和手持机的串口
     */
    public void connectCdRadioToCPU() throws IOException {
        writeFile(GPIO_CONTROL_DIRECTION,"1".getBytes());
    }

    /**
     * 连接CDRadio芯片和北斗芯片的串口
     */
    public void connectCDRadioToBeidou() throws IOException {
        writeFile(GPIO_CONTROL_DIRECTION,"0".getBytes());
    }
    private void writeFile(String path, byte[] buffer) throws IOException {
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(buffer);
        fos.close();
    }

    private static void showLog(String msg){
        Log.e("GPIOModel",msg);
    }
}
