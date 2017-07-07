package com.skycaster.geomapper.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * Created by 廖华凯 on 2017/6/29.
 */

public class ImageUtil {
    public static Bitmap getFixedWidthBitmap(String path, int targetWidth){
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds=true;
            BitmapFactory.decodeFile(path,options);
            int height = options.outHeight;
            int width = options.outWidth;
            options.inJustDecodeBounds=false;
            if(width>targetWidth){
                options.inSampleSize=width/targetWidth;
                showLog("options.outHeight: "+options.outHeight);
                showLog("options.outWidth: "+options.outWidth);
                showLog("targetWidth: "+targetWidth);
                showLog("options.inSampleSize: "+options.inSampleSize);
                options.inJustDecodeBounds=false;
                return BitmapFactory.decodeFile(path,options);
            }else {
                options.inJustDecodeBounds=false;
                height=targetWidth*height/width;
                return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path,options),targetWidth,height,false);
            }

    }

    public static void showLog(String msg){
        LogUtil.showLog("ImageUtil",msg);
    }


}
