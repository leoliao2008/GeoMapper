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
            options.inSampleSize=width/targetWidth;
            options.inJustDecodeBounds=false;
            return BitmapFactory.decodeFile(path,options);
    }


}
