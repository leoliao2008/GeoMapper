package com.skycaster.geomapper.models;

import android.os.Environment;

import java.io.File;

/**
 * Created by 廖华凯 on 2017/8/22.
 */

public class LocalStorageModel {

    public boolean isSdCardAvailable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    public File getDownLoadDir(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    public File getDataDir(){
        return Environment.getDataDirectory();
    }
}
