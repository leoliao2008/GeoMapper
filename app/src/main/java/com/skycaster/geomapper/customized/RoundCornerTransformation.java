package com.skycaster.geomapper.customized;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * Created by 廖华凯 on 2017/7/28.
 */

public class RoundCornerTransformation extends BitmapTransformation {
    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new BitmapShader(toTransform, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        Bitmap result=pool.get(width, height, Bitmap.Config.RGB_565);
        if(result==null){
            result=Bitmap.createBitmap(width, height,  Bitmap.Config.RGB_565);
        }
        Canvas canvas=new Canvas(result);
        canvas.drawRoundRect(new RectF(0,0, width, height),5f,5f,paint);
        return result;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {

    }
}
