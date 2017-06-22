package com.skycaster.geomapper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/21.
 */

public class RouteRecordOpenHelper extends SQLiteOpenHelper {
    private String lat="lat";
    private String lng="lng";
    private String mTableName;
    public RouteRecordOpenHelper(Context context,String tableName) {
        super(context, "route_points.db", null, 1);
        mTableName=tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String command="create table "+mTableName+"(loc_index INTEGER primary key autoincrement, "+ lat +" DOUBLE, "+
                lng+" DOUBLE);";
        LogUtil.showLog(getClass().getSimpleName(),command);
        db.execSQL(command);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +mTableName);
        onCreate(db);
    }

    public boolean saveRoutePoints(ArrayList<LatLng>list){
        SQLiteDatabase database = getWritableDatabase();
        onCreate(database);
        long result=-1;
        for(LatLng latLng:list){
            ContentValues cv=new ContentValues();
            cv.put(lat,latLng.latitude);
            cv.put(lng,latLng.longitude);
            result=database.insert(mTableName,null,cv);
            if(result==-1){
                break;
            }
        }
        database.close();
        return result!=-1;
    }

    public ArrayList<LatLng> getRoutePoints(){
        ArrayList<LatLng> list=new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(mTableName, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            LatLng latLng=new LatLng(cursor.getDouble(cursor.getColumnIndex(lat)),cursor.getDouble(cursor.getColumnIndex(lng)));
            list.add(latLng);
        }
        cursor.close();
        database.close();
        return list;
    }

    public void deleteRoute(String routeName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " +routeName);
        db.close();
    }

}
