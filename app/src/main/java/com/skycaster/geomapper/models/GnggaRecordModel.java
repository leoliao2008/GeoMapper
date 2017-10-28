package com.skycaster.geomapper.models;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/8/21.
 */

public class GnggaRecordModel {
    private static final long MINIMUM_SPACE =1024*1024*100;
    private File mDstFile;
    private BufferedOutputStream mOutputStream;

    public void prepareDestFile(Context context) throws IOException {
        stopRecording();
        Date date=new Date();
        mDstFile= generateDestFile(generateDir(date, context), date);
        if(mDstFile!=null&&mDstFile.exists()){
            mOutputStream=new BufferedOutputStream(new FileOutputStream(mDstFile));
        }
    }

    public void write(byte[] data) throws IOException {
        if(mOutputStream!=null){
            mOutputStream.write(data);
            mOutputStream.write("\r\n".getBytes());
            mOutputStream.flush();
        }
    }

    public void stopRecording() throws IOException {
        if(mOutputStream!=null){
            mOutputStream.flush();
            mOutputStream.close();
            mOutputStream=null;
        }
    }


    private synchronized File generateDir(Date date, Context context){
        File dir=null;
        String dirPath="/"+ context.getPackageName()+"/"+new SimpleDateFormat("yyyyMMdd").format(date);
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            if(Environment.getExternalStorageDirectory().getFreeSpace()> MINIMUM_SPACE){
                dir=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+dirPath);
                showLog("getExternalStoragePublicDirectory is selected");
            }else {
                showLog("getExternalStorageDirectory free space not enough");
            }
        }else {
            if(Environment.getDataDirectory().getFreeSpace()> MINIMUM_SPACE){
                dir=new File(Environment.getDataDirectory().getAbsolutePath()+dirPath);
                showLog("getDataDirectory is selected");
            }else {
                showLog("getDataDirectory free space not enough");
            }
        }
        if(dir!=null){
            if(!dir.exists()){
                if(dir.mkdirs()){
                    showLog("document_icon created: "+dir.getAbsolutePath());
                }else {
                    showLog("document_icon fail to create");
                    showLog("try to create document_icon in public document_icon DIRECTORY_DCIM ...");
                    dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                }
            }else {
                showLog("document_icon already existed, no new document_icon is made");
            }
        }else {
            showLog("neither getExternalStoragePublicDirectory nor getDataDirectory is available, document_icon =null");
        }
        return dir;
    }

    private File generateDestFile(File dir, Date date){
        if(dir==null||!dir.exists()){
            showLog("track record files fail to create for document_icon =null or document_icon not exists");
        }else {
            String s = new SimpleDateFormat("HHmmss", Locale.CHINA).format(date) + ".txt";
            String destFileName="GNGGA_Data_"+s;
            File file = generateDestFile(dir, destFileName);
            if(file!=null&&file.exists()){
                return file;
            }else {
                showLog("biz file init fail for file=null or file not exist");
            }
        }
        return null;
    }


    private File generateDestFile(File dir, String fileName){
        File des=new File(dir,fileName);
        if(des.exists()){
            if(des.delete()){
                showLog(des.getName()+"exists, but be deleted successfully");
            }else {
                showLog(des.getName()+"exists, and fail to delete");
            }
        }
        try {
            if(des.createNewFile()){
                showLog(des.getName()+" created");
            }else {
                showLog(des.getName()+"fail to create without exception reason, may be it already exists");
            }
        } catch (IOException e) {
            showLog(des.getName()+"fails to create for exception: "+e.getMessage());
        }
        return des;
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }

}
