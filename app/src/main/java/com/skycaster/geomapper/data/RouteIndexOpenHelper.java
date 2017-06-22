package com.skycaster.geomapper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.baidu.mapapi.model.LatLng;
import com.skycaster.geomapper.base.BaseApplication;
import com.skycaster.geomapper.interfaces.SQLiteExecuteResultCallBack;
import com.skycaster.geomapper.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/6/21.
 */

public class RouteIndexOpenHelper extends SQLiteOpenHelper {
    private Context mContext;
    private String mTableName ="route_index";
    private String mRouteName ="route_name";
    public RouteIndexOpenHelper(Context context) {
        super(context, "route_index.db", null, 1);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createIndex = "create table "+ mTableName +"(route_index INTEGER primary key autoincrement, "+ mRouteName +" varchar(20) not null);";
        LogUtil.showLog(getClass().getSimpleName(),createIndex);
        db.execSQL(createIndex);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +mTableName);
        onCreate(db);

    }

    public ArrayList<String> getRouteIndex(){
        ArrayList<String> list=new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(mTableName, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            String s = cursor.getString(cursor.getColumnIndex(mRouteName));
            if(!TextUtils.isEmpty(s)){
                list.add(s);
            }
        }
        cursor.close();
        return list;
    }

    public void addRoute(final String routeName, final ArrayList<LatLng>routePoints, final SQLiteExecuteResultCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RouteRecordOpenHelper openHelper=new RouteRecordOpenHelper(mContext,routeName);
                boolean isSave = openHelper.saveRoutePoints(routePoints);
                if(isSave){
                    ContentValues cv=new ContentValues();
                    cv.put(mRouteName,routeName);
                    final long result=getWritableDatabase().insert(mTableName,null,cv);
                    BaseApplication.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onResult(result!=-1);
                        }
                    });
                }
                openHelper.close();
            }
        }).start();
    }

    public boolean deleteRoute(String routeName){
        RouteRecordOpenHelper helper=new RouteRecordOpenHelper(mContext,routeName);
        helper.deleteRoute(routeName);
        helper.close();
        SQLiteDatabase db = getWritableDatabase();
        long result= db.delete(mTableName, this.mRouteName + "=?", new String[]{routeName});
        return result!=-1;
    }


}
