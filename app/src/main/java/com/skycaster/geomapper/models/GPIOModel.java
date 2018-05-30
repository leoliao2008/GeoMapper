package com.skycaster.geomapper.models;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 廖华凯 on 2017/10/21.
 */

public class GPIOModel {

    private static final String SK9042_POW = "/sys/class/gpio/gpio209/value";
    private static final String SK9042_RESET ="/sys/class/gpio/gpio215/value";//取消这个功能了
    private static final String GPS_POW="/sys/class/gpio/gpio210/value";


    public void toggleSk9042Pow(boolean isToTurnOn) throws IOException {
        writeFile(SK9042_POW,isToTurnOn?"1".getBytes():"0".getBytes());
    }

    public void toggleGPSPow(boolean isToTurnOn) throws IOException {
        writeFile(GPS_POW,isToTurnOn?"1".getBytes():"0".getBytes());
    }

    public void turnOnAllModulesPow() throws IOException {
        toggleAllModulesPow(true);
    }

    public void turnOffAllModulesPow() throws IOException {
        toggleAllModulesPow(false);
    }

    private void toggleAllModulesPow(boolean isToTurnOn) throws IOException{
        toggleGPSPow(isToTurnOn);
        toggleSk9042Pow(isToTurnOn);
    }

    private void writeFile(String path, byte[] buffer) throws IOException {
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(buffer);
        fos.close();
    }



}
