package com.skycaster.geomapper.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/10.
 */

public class MappingDataOpenHelper extends SQLiteOpenHelper{
    private Context mContext;
    private String table_name="Mapping_Data";
    private String title="Data_Title";
    private String date="Data_Date";
    private String id="Data_ID";
    private String content="Data_Content";
    public MappingDataOpenHelper(Context context) {
        super(context, "mapping_data.db", null, 1);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table Mapping_Data (Data_Index INTEGER primary key autoincrement, " +
                "Date_Title varchar, " +
                "Date_Date varchar, " +
                "Date_ID INTEGER, " +
                "Date_Content BLOB)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql="drop table if exists Mapping_Data";
        db.execSQL(sql);
        onCreate(db);
    }

    public boolean add(String title, ArrayList<LatLng> content){

        return false;
    }
}
