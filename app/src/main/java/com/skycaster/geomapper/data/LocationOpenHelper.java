package com.skycaster.geomapper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skycaster.geomapper.R;
import com.skycaster.geomapper.bean.Location;
import com.skycaster.geomapper.bean.Tag;
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
    private String mLocationIcon="location_icon";
    private String mLatitude="loaction_latitude";
    private String mLongitude="location_longitude";
    private String mAltitude="location_altitude";
    private String mComments="location_comments";
    private String mTagName="location_tag_name";
    private String mTagId="location_tag_id";
    private String mIsBaiduCoordinate="location_is_baidu_coord";
    private String mPicList="location_pic_list";
    private String mDate="location_date";
    private Context mContext;
    private String sqlCreateNewTable ="create table "+mTableName+" (location_index INTEGER primary key autoincrement, "
            +mLocationName+" varchar(20), "+mLocationIcon+" INTEGER, "+mLatitude+" DOUBLE, "+mLongitude
            +" DOUBLE, "+mAltitude+" DOUBLE, "+mComments+" TEXT, "+mTagName+" varchar(20), "+mTagId+" INTEGER, "
            +mPicList+" TEXT, "+mIsBaiduCoordinate+" SHORT, "+mDate+" varchar(20));";
    private String sqlRenameOldTable ="alter table "+mTableName+" rename to temp_table;";
    private String sqlDeleteOldTable ="drop table temp_table;";
    private SQLiteDatabase mDefaultDataBase;
    public static LocationOpenHelper getInstance(Context context){
        if(openHelper==null){
            openHelper=new LocationOpenHelper(context);
        }
        return openHelper;
    }

    private LocationOpenHelper(Context context) {
        super(context, "location.db", null, 4);
        mContext=context;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        if(mDefaultDataBase!=null&&mDefaultDataBase.isOpen()){
            return mDefaultDataBase;
        }
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        if(mDefaultDataBase!=null&&mDefaultDataBase.isOpen()){
            return mDefaultDataBase;
        }
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDefaultDataBase=db;
        db.execSQL(sqlCreateNewTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mDefaultDataBase=db;
        if(oldVersion==3&&newVersion==4){
            db.execSQL(sqlRenameOldTable);
            db.execSQL(sqlCreateNewTable);
            Cursor cursor = db.query("temp_table", null, null, null, null, null, null);
            while (cursor.moveToNext()){
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex(mData));
                Location location = toLocation(bytes);
                insert(location);
            }
            cursor.close();
            db.execSQL(sqlDeleteOldTable);
        }else {
            String sql="DROP TABLE IF EXISTS " + mTableName;
            db.execSQL(sql);
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        mDefaultDataBase=db;
    }


    public boolean insert(Location location){
        long result=-1;
        SQLiteDatabase db = getWritableDatabase();
        if(!checkIfDuplicateName(location.getTitle(),db)){
            result=db.insert(mTableName,null, getContentValue(location));
        }
        return result>0;
    }

    private ContentValues getContentValue(Location location){
        ContentValues ctv=new ContentValues();
        ctv.put(mLocationName,location.getTitle());
        ctv.put(mLocationIcon,location.getIconStyle());
        ctv.put(mComments,location.getComments());
        ctv.put(mAltitude,location.getAltitude());
        ctv.put(mLatitude,location.getLatitude());
        ctv.put(mLongitude,location.getLongitude());
        ctv.put(mTagName,location.getTag().getTagName());
        ctv.put(mTagId,location.getTag().getId());
        ctv.put(mDate,location.getSubmitDate());
        ctv.put(mIsBaiduCoordinate,location.isBaiduCoordinateSystem());
        String picList = new Gson().toJson(location.getPicList());
        ctv.put(mPicList,picList);
        return ctv;
    }

    public boolean alter(Location before,Location after){
        boolean result;
        SQLiteDatabase db = getWritableDatabase();
        showLog("name before ="+before.getTitle()+"   name after ="+after.getTitle());
        if(!before.getTitle().equals(after.getTitle())){
            showLog("a different name is made to replace the former name.");
            if(checkIfDuplicateName(after.getTitle(),db)){
                showLog("Duplicate name is found. Abort.");
                Toast.makeText(mContext,mContext.getString(R.string.duplicate_data),Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        int delete = db.delete(mTableName, mLocationName + "=?", new String[]{before.getTitle()});
        result=delete>0;
        showLog("delete is success: "+result);
        if(result){
            result=insert(after);
            showLog("insert is success: "+result);
        }
        return result;
    }

    public int updateTag(String oldTagName,String newTagName){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues ctv=new ContentValues();
        ctv.put(mTagName,newTagName);
        return database.update(mTableName,ctv,mTagName+"=?",new String[]{oldTagName});
    }

    public ArrayList<Location> getLocationList(){
        ArrayList<Location> list=new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(mTableName, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            list.add(toLocation(cursor));
        }
        cursor.close();

        return list;
    }

    public boolean delete(Location location){
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(mTableName,mLocationName+"=?",new String[]{location.getTitle()})>0;
    }

    public boolean deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = db.delete(mTableName, null, null);
        return rowsAffected>0;
    }


    public boolean checkIfDuplicateName(String name,SQLiteDatabase db) {
        boolean isDuplicate=false;
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

    public boolean checkIfDuplicateName(String name) {
        return checkIfDuplicateName(name,getReadableDatabase());
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

    private Location toLocation(Cursor cursor){
//        String title="null";
//        int iconStyle;
//        double latitude;
//        double longitude;
//        double altitude;
//        String comments="null";
//        Tag tag;
//        ArrayList<String> picList;
//        boolean isBaiduCoordinateSystem;
//        String submitDate="null";
        Location location=new Location();
        location.setTitle(cursor.getString(cursor.getColumnIndex(mLocationName)));
        location.setIconStyle(cursor.getInt(cursor.getColumnIndex(mLocationIcon)));
        location.setLatitude(cursor.getDouble(cursor.getColumnIndex(mLatitude)));
        location.setLongitude(cursor.getDouble(cursor.getColumnIndex(mLongitude)));
        location.setAltitude(cursor.getDouble(cursor.getColumnIndex(mAltitude)));
        location.setComments(cursor.getString(cursor.getColumnIndex(mComments)));
        location.setTag(new Tag(
                cursor.getString(cursor.getColumnIndex(mTagName)),
                cursor.getInt(cursor.getColumnIndex(mTagId))
        ));
        short isBaiduCoord = cursor.getShort(cursor.getColumnIndex(mIsBaiduCoordinate));
        location.setBaiduCoordinateSystem(isBaiduCoord>0);
        String json = cursor.getString(cursor.getColumnIndex(mPicList));
        ArrayList<String> picPaths = new Gson().fromJson(json, new TypeToken<ArrayList<String>>() {}.getType());
        location.setPicList(picPaths);
        return location;
    }

    private void showLog(String msg){
        LogUtil.showLog(getClass().getSimpleName(),msg);
    }


}
