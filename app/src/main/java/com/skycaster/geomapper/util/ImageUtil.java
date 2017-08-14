package com.skycaster.geomapper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.base.BaseApplication;

import java.io.File;

import static android.graphics.BitmapFactory.decodeFile;


/**
 * Created by 廖华凯 on 2017/6/29.
 */

public class ImageUtil {
    private static LruCache<String,Bitmap> cache=new LruCache<>(1024*1024*4);
    private static SharedPreferences sp=BaseApplication.getContext().getSharedPreferences("pic_size_back_up", Context.MODE_PRIVATE);
    private static RequestOptions options=new RequestOptions()
            .encodeFormat(Bitmap.CompressFormat.PNG)
            .centerCrop()
            .encodeQuality(10)
            .placeholder(R.drawable.ic_loading_128px)
            .error(R.drawable.pic_file_deleted)
            .fallback(R.drawable.blank_file)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .downsample(DownsampleStrategy.AT_LEAST);
    private static int screenWidthPx = (int) (BaseApplication.getScreenWidth()*0.95);
    private static int screenWidthDp = (int) (BaseApplication.getScreenWidth()/BaseApplication.getDisplayMetrics().density+1.0f);


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
        if(height>0){
            return height;
        }
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path,options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        if(outWidth>0&&outHeight>0){
            height = (float) (width * outHeight / outWidth);
            sp.edit().putFloat(key,height).apply();
        }else {
            sp.edit().remove(key).apply();
        }
//        showLog("Width = "+width+" , Height = "+height);
        return height;
    }

    public static void LoadImageWithGlide(final ImageView targetView, String filePath){
        float height = calculateHeight(filePath, screenWidthPx);
        if(height>0){
            options.override(screenWidthPx, (int) (height +0.5f));
            Glide.with(targetView.getContext()).asBitmap().apply(options).transition(BitmapTransitionOptions.withCrossFade())
                    .load(filePath).into(targetView);
        }else {
            targetView.setImageResource(R.drawable.pic_file_deleted);
        }
    }

    public static void LoadImageWidthFresco(final SimpleDraweeView targetView, String filePath){

        final File source = new File(filePath);
        showLog("Item : "+source.getAbsolutePath());
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(source.getPath(), options);
        int heightPx = options.outHeight;
        int widthPx = options.outWidth;
        int heightDp =0;
        if(widthPx>0){
            heightDp =heightPx * screenWidthDp/widthPx;
        }
        showLog("screenWidthDp = "+screenWidthDp+", screenHeightDp = "+heightDp);
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.fromFile(source))
                .setResizeOptions(ResizeOptions.forDimensions(screenWidthDp, heightDp))
                .build();


        AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(targetView.getController())
                .setImageRequest(imageRequest)
                .setControllerListener(new ControllerListener<ImageInfo>() {
                    @Override
                    public void onSubmit(String id, Object callerContext) {

                    }


                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        int outHeight = imageInfo.getHeight();
                        int outWidth = imageInfo.getWidth();
                        showLog("imageInfo width = "+outWidth+", imageInfo height= "+outHeight);
                        int width = screenWidthPx;
                        int height = width * outHeight / outWidth;
                        ViewGroup.LayoutParams params = targetView.getLayoutParams();
                        params.height = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
                        targetView.setLayoutParams(params);
                    }


                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {

                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {

                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {


                    }

                    @Override
                    public void onRelease(String id) {

                    }
                })
                .build();
        targetView.setController(controller);
    }


    public static void showLog(String msg){
        LogUtil.showLog("ImageUtil",msg);
    }



}
