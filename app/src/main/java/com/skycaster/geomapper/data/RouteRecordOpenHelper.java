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
    private Context mContext;
    private String  mDataBaseName;
    private String mTableName;
    public RouteRecordOpenHelper(Context context, String name) {
        super(context, name+".db", null, 1);
        mContext=context;
        mDataBaseName=name;
        mTableName=mDataBaseName+"_table";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String command="create table "+mTableName+"(route_point INTEGER primary key autoincrement, "+ lat +" DOUBLE, "+
                lng+" DOUBLE);";
        LogUtil.showLog(getClass().getSimpleName(),command);
        db.execSQL(command);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean saveRoutePoints(ArrayList<LatLng>list){
        SQLiteDatabase database = getWritableDatabase();
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
        return list;
    }

}
