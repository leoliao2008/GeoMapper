package com.skycaster.geomapper.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.skycaster.geomapper.base.BaseApplication;

/**
 * Created by 廖华凯 on 2017/6/29.
 */

public class UriUtil {
    public static String getLocalFilePath(Uri uri){
        String path=null;
        ContentResolver resolver = BaseApplication.getContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
        if (cursor!=null&&cursor.moveToNext()){
            path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            cursor.close();
        }
        return path;
    }
}
