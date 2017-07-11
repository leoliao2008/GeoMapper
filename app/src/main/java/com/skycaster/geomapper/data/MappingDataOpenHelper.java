package com.skycaster.geomapper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.bean.MyLatLng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2017/7/10.
 */

public class MappingDataOpenHelper extends SQLiteOpenHelper{
    private Context mContext;
    private String mTableName ="Mapping_Data";
    private String mDataTitle ="Data_Title";
    private String mDataDate ="Data_Date";
    private String mDataCoord ="Data_Coordinates";
    private SimpleDateFormat mDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public MappingDataOpenHelper(Context context) {
        super(context, "mapping_data.db", null, 1);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table "+ mTableName +" (Data_Index INTEGER primary key autoincrement, " +
                mDataTitle+" varchar, " +
                mDataDate+" varchar, " +
                mDataCoord +" BLOB)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql="DROP TABLE IF EXISTS "+mTableName;
        db.execSQL(sql);
        onCreate(db);
    }

    public boolean add(String title, ArrayList<LatLng> content){
        long result=-1;
        SQLiteDatabase db = getWritableDatabase();
        if(!checkIsDuplicate(title)){
            ContentValues cv=new ContentValues();
            cv.put(mDataTitle,title);
            cv.put(mDataDate,mDateFormat.format(new Date()));
            cv.put(mDataCoord,toByteArray(content));
            result=db.insert(mTableName,null,cv);
        }
        return result>0;
    }

//    public boolean delete()

    private boolean checkIsDuplicate(String title){
        boolean isDuplicate=false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(mTableName, new String[]{mDataTitle}, null, null, null, null, null);
        while (cursor.moveToNext()){
            String temp = cursor.getString(cursor.getColumnIndex(mDataTitle));
            if(temp!=null&&title.equals(temp)){
                isDuplicate=true;
                break;
            }
        }
        cursor.close();
        return isDuplicate;
    }

    private byte[] toByteArray(ArrayList<LatLng>content){
        byte[] result=null;
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ArrayList<MyLatLng> myLatLngs=new ArrayList<>();
        for(LatLng latLng:content){
            myLatLngs.add(new MyLatLng(latLng));
        }
        ObjectOutputStream oos=null;
        try {
            oos=new ObjectOutputStream(bos);
            oos.writeObject(myLatLngs);
            oos.flush();
            result=bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(oos!=null){
                    oos.close();
                }
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private ArrayList<LatLng> getContents(byte[] content){
        ArrayList<LatLng> result=new ArrayList<>();
        ByteArrayInputStream bis=new ByteArrayInputStream(content);
        ObjectInputStream ois=null;
        try {
            ois=new ObjectInputStream(bis);
            ArrayList<MyLatLng> myLatLngs = (ArrayList<MyLatLng>) ois.readObject();
            if(myLatLngs!=null){
                for(MyLatLng myLatLng:myLatLngs){
                    result.add(new LatLng(myLatLng.getLat(),myLatLng.getLng()));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if(ois!=null){
                    ois.close();
                }
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
