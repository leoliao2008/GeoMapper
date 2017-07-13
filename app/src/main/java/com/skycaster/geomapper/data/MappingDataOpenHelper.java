package com.skycaster.geomapper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skycaster.geomapper.bean.MappingData;
import com.skycaster.geomapper.bean.MyLatLng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/10.
 */

public class MappingDataOpenHelper extends SQLiteOpenHelper{
    private Context mContext;
    private String mTableName ="Default_Table";
    private String mDataTitle ="Data_Title";
    private String mDataComment="Data_Comment";
    private String mDataAddress ="Data_Address";
    private String mDataAdjacent="Data_Adjacent_Description";
    private String mDataPerimeter="Data_Perimeter";
    private String mDataArea="Data_Area";
    private String mDataId="Data_Id";
    private String mDataDate ="Data_Date";
    private String mDataCoord ="Data_Coordinates";
    private String mDataPathLength="Data_PathLength";
    private String mDataTagID="Data_TagID";
    private String mDataTagName="Data_TagName";

    public MappingDataOpenHelper(Context context) {
        super(context, "mapping_data.db", null, 3);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table "+ mTableName +" (Data_Index INTEGER primary key autoincrement, " +
                mDataTitle+" varchar, " +
                mDataComment+" varchar, "+
                mDataAddress +" varchar, "+
                mDataAdjacent+" varchar, "+
                mDataPathLength+" DOUBLE, "+
                mDataPerimeter+" DOUBLE, "+
                mDataArea+" DOUBLE, "+
                mDataId+ " LONG, "+
                mDataDate+" varchar, " +
                mDataTagID+" INTEGER, "+
                mDataTagName+" varchar, "+
                mDataCoord +" TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql="DROP TABLE IF EXISTS "+mTableName;
        db.execSQL(sql);
        onCreate(db);
    }

    public boolean add(MappingData data){
        long result=-1;
        if(!checkIsDuplicate(data.getId())){
            SQLiteDatabase db = getWritableDatabase();
            result=db.insert(mTableName,null,initContentValue(data));
        }
        return result>0;
    }

    private ContentValues initContentValue(MappingData data) {
        ContentValues cv=new ContentValues();
        cv.put(mDataTitle,data.getTitle());
        cv.put(mDataComment,data.getComment());
        cv.put(mDataAddress,data.getAddress());
        cv.put(mDataAdjacent,data.getAdjacentLoc());
        cv.put(mDataPathLength,data.getPathLength());
        cv.put(mDataPerimeter,data.getPerimeter());
        cv.put(mDataArea,data.getArea());
        cv.put(mDataId,data.getId());
        cv.put(mDataDate,data.getDate());
        cv.put(mDataTagName,data.getTagName());
        cv.put(mDataTagID,data.getTagID());
        ArrayList<MyLatLng> latLngs = data.getLatLngs();
        String json = new Gson().toJson(latLngs);
        cv.put(mDataCoord,json);
        return cv;
    }

    public boolean delete(MappingData data){
        SQLiteDatabase db = getWritableDatabase();
        long result=db.delete(mTableName,mDataId+"=?",new String[]{String.valueOf(data.getId())});
        return result>0;
   }

   public boolean edit(MappingData newData){
        SQLiteDatabase db = getWritableDatabase();
        long result=db.update(mTableName,initContentValue(newData),mDataId+"=?",new String[]{String.valueOf(newData.getId())});
        return result>0;
   }

   public ArrayList<MappingData> getMappingDatas(){
       ArrayList<MappingData>list=new ArrayList<>();
       SQLiteDatabase db = getReadableDatabase();
       Cursor cursor = db.query(mTableName, null, null, null, null, null, null);
       while (cursor.moveToNext()){
           String title=cursor.getString(cursor.getColumnIndex(mDataTitle));
           String address=cursor.getString(cursor.getColumnIndex(mDataAddress));
           String adjacent=cursor.getString(cursor.getColumnIndex(mDataAdjacent));
           String comments=cursor.getString(cursor.getColumnIndex(mDataComment));
           double pathLen=cursor.getDouble(cursor.getColumnIndex(mDataPathLength));
           double perimeter=cursor.getDouble(cursor.getColumnIndex(mDataPerimeter));
           double area=cursor.getDouble(cursor.getColumnIndex(mDataArea));
           long id=cursor.getLong(cursor.getColumnIndex(mDataId));
           String tagName = cursor.getString(cursor.getColumnIndex(mDataTagName));
           int tagID = cursor.getInt(cursor.getColumnIndex(mDataTagID));
           String date = cursor.getString(cursor.getColumnIndex(mDataDate));
           String jason=cursor.getString(cursor.getColumnIndex(mDataCoord));
           ArrayList<MyLatLng> coords = new Gson().fromJson(jason, new TypeToken<ArrayList<MyLatLng>>() {}.getType());
           MappingData data=new MappingData(
                   title,
                   coords,
                   comments,
                   address,
                   adjacent,
                   pathLen,
                   perimeter,
                   area,
                   id,
                   date,
                   tagID,
                   tagName
           );
           list.add(data);
       }
       cursor.close();
       return list;
   }

    private boolean checkIsDuplicate(long id){
        boolean isDuplicate=false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(mTableName, new String[]{mDataId}, null, null, null, null, null);
        while (cursor.moveToNext()){
            long temp = cursor.getLong(cursor.getColumnIndex(mDataId));
            if(id==temp){
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
            myLatLngs.add(new MyLatLng(latLng.latitude,latLng.longitude,0));
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
