package com.skycaster.geomapper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/21.
 */

public class RouteIndexOpenHelper extends SQLiteOpenHelper {
    private Context mContext;
    private String tableName="route_index";
    private String routeName ="route_name";
    public RouteIndexOpenHelper(Context context) {
        super(context, "route_record.db", null, 1);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String command = "create table "+tableName+"(route_index INTEGER primary key autoincrement, "+ routeName +" varchar(20) not null);";
        LogUtil.showLog(getClass().getSimpleName(),command);
        db.execSQL(command);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<String> getRouteIndex(){
        ArrayList<String> list=new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(tableName, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            String s = cursor.getString(cursor.getColumnIndex(routeName));
            if(!TextUtils.isEmpty(s)){
                list.add(s);
            }
        }
        cursor.close();
        return list;
    }

    public boolean addRoute(String routeName,ArrayList<LatLng>routePoints){
        RouteRecordOpenHelper openHelper=new RouteRecordOpenHelper(mContext,routeName);
        boolean isSave = openHelper.saveRoutePoints(routePoints);
        long result=-1;
        if(isSave){
            ContentValues cv=new ContentValues();
            cv.put(this.routeName,routeName);
            result=getWritableDatabase().insert(tableName,null,cv);
        }
        return result!=-1;
    }

    public boolean deleteRoute(String routeName){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS " +routeName);
        long result= database.delete(tableName, this.routeName + "=?", new String[]{routeName});;
        return result!=-1;
    }


}
