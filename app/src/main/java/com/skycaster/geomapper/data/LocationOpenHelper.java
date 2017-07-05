package com.skycaster.geomapper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.skycaster.geomapper.R;
import com.skycaster.geomapper.bean.Location;
import com.skycaster.geomapper.util.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/29.
 */

public class LocationOpenHelper extends SQLiteOpenHelper {
    private static LocationOpenHelper openHelper;
    private String mTableName="locations";
    private String mLocationName="location_name";
    private String mData="location_data";
    private Context mContext;
    public static LocationOpenHelper getInstance(Context context){
        if(openHelper==null){
            openHelper=new LocationOpenHelper(context);
        }
        return openHelper;
    }

    private LocationOpenHelper(Context context) {
        super(context, "location.db", null, 3);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table "+mTableName+" (location_index INTEGER primary key autoincrement, "+mLocationName+" varchar(20), "+mData+" BLOB)";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql="DROP TABLE IF EXISTS " + mTableName;
        db.execSQL(sql);
        onCreate(db);
    }


    public boolean insert(Location location){
        long result=-1;
        if(!checkIfDuplicateName(location.getTitle())){
            ContentValues cv=new ContentValues();
            cv.put(mLocationName,location.getTitle());
            cv.put(mData,toByteArray(location));
            SQLiteDatabase db = getWritableDatabase();
            result=db.insert(mTableName,null,cv);
        }
        return result>0;
    }

    public boolean alter(Location before,Location after){
        boolean result;
        showLog("name before ="+before.getTitle()+"   name after ="+after.getTitle());
        if(!before.getTitle().equals(after.getTitle())){
            showLog("a different name is made to replace the former name.");
            if(checkIfDuplicateName(after.getTitle())){
                showLog("Duplicate name is found. Abort.");
                Toast.makeText(mContext,mContext.getString(R.string.duplicate_data),Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        SQLiteDatabase db = getWritableDatabase();
        int delete = db.delete(mTableName, mLocationName + "=?", new String[]{before.getTitle()});
        result=delete>0;
        showLog("delete is success: "+result);
        if(result){
            result=insert(after);
            showLog("insert is success: "+result);
        }
        return result;
    }

    public ArrayList<Location> getLocationList(){
        ArrayList<Location> list=new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(mTableName, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex(mData));
            list.add(toLocation(bytes));
        }
        cursor.close();

        return list;
    }

    public boolean delete(Location location){
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(mTableName,mLocationName+"=?",new String[]{location.getTitle()})>0;
    }

    public boolean deleteAll(){
        boolean isSuccess=true;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(mTableName, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(mLocationName));
            isSuccess=db.delete(mTableName,mLocationName+"=?",new String[]{name})>0;
            if(!isSuccess){
                break;
            }
        }
        cursor.close();
        return isSuccess;
    }


    public boolean checkIfDuplicateName(String name) {
        boolean isDuplicate=false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(mTableName, new String[]{mLocationName}, null, null, null, null, null);
        while (cursor.moveToNext()){
            String s = cursor.getString(cursor.getColumnIndex(mLocationName));
            if(name.equals(s)){
                isDuplicate=true;
                break;
            }
        }
        cursor.close();
        return isDuplicate;
    }

    private byte[] toByteArray(Object obj){
        byte[] bytes=null;
        ByteArrayOutputStream arrayOutputStream=new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            bytes = arrayOutputStream.toByteArray();
            arrayOutputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(bytes!=null){
            return bytes.clone();
        }else {
            return null;
        }
    }

    private Location toLocation(byte[] bytes){
        Location location=null;
        try {
            ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(bytes));
            try {
                location = (Location) ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                ois.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return location;
    }

    private void showLog(String msg){
        LogUtil.showLog(getClass().getSimpleName(),msg);
    }


}
