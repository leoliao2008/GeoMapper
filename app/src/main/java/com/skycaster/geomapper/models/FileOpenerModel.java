package com.skycaster.geomapper.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.skycaster.geomapper.base.BaseApplication;

import java.io.File;

/**
 * Created by 廖华凯 on 2017/8/22.
 */

public class FileOpenerModel {

    public String getMimeType(String path){
        String type=null;
        if(path.endsWith(".txt")){
            type="text/plain";
        }else if(path.endsWith(".pdf")){
            type="application/pdf";
        }else if(path.endsWith(".png")){
            type="image/png";
        }else if(path.endsWith(".rmvb")){
            type="application/vnd.rn-realmedia-vbr";
        }else if(path.endsWith(".xml")||path.endsWith(".xsl")){
            type="text/xml";
        }else if(path.endsWith(".apk")){
            type="application/vnd.android.package-archive";
        }else if(path.endsWith(".mp3")){
            type="audio/mp3";
        }else if(path.endsWith(".html")){
            type="text/html";
        }else if(path.endsWith(".img")){
            type="application/x-img";
        }else if(path.endsWith(".jpg")){
            type="image/jpeg";
        }else if(path.endsWith(".doc")){
            type="application/msword";
        }else if(path.endsWith(".mp4")){
            type="video/mpeg4";
        }
        return type;
    }

    public void openFile(Context context,File file){
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String mimeType = getMimeType(file.getAbsolutePath());
        if(TextUtils.isEmpty(mimeType)){
            BaseApplication.showToast("无法辨识的文件格式。");
        }else {
            intent.setDataAndType(Uri.fromFile(file),mimeType);
            context.startActivity(intent);
        }
    }
}
