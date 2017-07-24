package com.skycaster.geomapper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.skycaster.geomapper.base.BaseApplication;

import static android.graphics.BitmapFactory.decodeFile;


/**
 * Created by 廖华凯 on 2017/6/29.
 */

public class ImageUtil {
    private static LruCache<String,Bitmap> cache=new LruCache<>(1024*1024*4);
    private static SharedPreferences sp=BaseApplication.getContext().getSharedPreferences("pic_size_back_up", Context.MODE_PRIVATE);

    public static Bitmap getFixedWidthBitmap(String path, int targetWidth){
        String key=path+String.valueOf(targetWidth);
        Bitmap bitmap=cache.get(key);
        if(bitmap!=null){
            return bitmap;
        }
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        decodeFile(path,options);
        int height = options.outHeight;
        int width = options.outWidth;
        if(height>0&&width>0){
            options.inJustDecodeBounds=false;
            if(width>targetWidth){
                options.inSampleSize=width/targetWidth;
                showLog("options.outHeight: "+options.outHeight);
                showLog("options.outWidth: "+options.outWidth);
                showLog("targetWidth: "+targetWidth);
                showLog("options.inSampleSize: "+options.inSampleSize);
                options.inJustDecodeBounds=false;
                bitmap= BitmapFactory.decodeFile(path,options);
            }else {
                options.inJustDecodeBounds=false;
                height=targetWidth*height/width;
                bitmap= Bitmap.createScaledBitmap(decodeFile(path,options),targetWidth,height,false);
            }
        }
        if(bitmap!=null){
            cache.put(key,bitmap);
        }
        return bitmap;
    }

    public static float calculateHeight(String path,double width) {
        String key=path+String.valueOf(width);
        float height=sp.getFloat(key,0);
        if(height!=0){
            return height;
        }
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path,options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        if(outWidth>0&&outHeight>0){
            height = (float) (width * outHeight / outWidth);
        }
        return height;
    }

    public static void showLog(String msg){
        LogUtil.showLog("ImageUtil",msg);
    }


}
