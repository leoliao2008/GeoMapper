package com.skycaster.geomapper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.skycaster.geomapper.bean.Tag;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 廖华凯 on 2017/6/28.
 */

public class MappingDataTagsOpenHelper extends SQLiteOpenHelper {

    private String mTableName ="mapping_data_tags";
    private Integer mID;

    public MappingDataTagsOpenHelper(Context context) {
        super(context, "mapping_data_tags.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table "+ mTableName +" (tag_index INTEGER primary key autoincrement, name varchar(20) not null, id INTEGER)";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + mTableName);
        onCreate(db);
    }

    public ArrayList<Tag> getTagList(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Tag> list=new ArrayList<>();
        Cursor cursor = db.query(mTableName, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            String name=cursor.getString(cursor.getColumnIndex("name"));
            int id=cursor.getInt(cursor.getColumnIndex("id"));
            if(name!=null){
                list.add(new Tag(name,id));
            }
        }
        cursor.close();
        return list;
    }

    public boolean delete(Tag tag){
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(mTableName, "id=?", new String[]{String.valueOf(tag.getId())});
        return result>0;
    }

    public boolean add(String name,int id){
        long result=-1;
        if(!isContain(id)){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv=new ContentValues();
            cv.put("name",name);
            cv.put("id",id);
            result = db.insert(mTableName, null, cv);
        }
        return result>0;
    }

    public boolean add(String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("id", generateID());
        return db.insert(mTableName, null, cv) > 0;
    }

    private boolean isContain(int id){
        boolean isContain=false;
        SQLiteDatabase rdb = getReadableDatabase();
        Cursor cursor = rdb.query(mTableName, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            int i=cursor.getInt(cursor.getColumnIndex("id"));
            if(i==id){
                isContain=true;
                break;
            }
        }
        cursor.close();
        return isContain;
    }

    public boolean alter(String formerName,int formerId,String newName,int newId){
        if(formerName.equals(newName)&&formerId==newId){
            return true;
        }
        long result=-1;
        boolean isContain=false;
        SQLiteDatabase rdb = getReadableDatabase();
        Cursor cursor = rdb.query(mTableName, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            int i=cursor.getInt(cursor.getColumnIndex("id"));
            if(i==newId){
                if(!cursor.getString(cursor.getColumnIndex("name")).equals(formerName)){
                    isContain=true;
                    break;
                }
            }
        }
        cursor.close();
        if(!isContain){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv=new ContentValues();
            cv.put("name",newName);
            cv.put("id",newId);
            result=db.update(mTableName,cv,"id=?",new String[]{String.valueOf(formerId)});
        }
        return result>0;
    }

    private int generateID() {
        int id;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(mTableName, null, null, null, null, null, null);
        ArrayList<Integer> list=new ArrayList<>();
        while (cursor.moveToNext()){
            list.add(cursor.getInt(cursor.getColumnIndex("id")));
        }
        id=new Random().nextInt(98)+1;
        while (list.contains(id)){
            id=new Random().nextInt(98)+1;
        }
        cursor.close();
        return id;
    }


}
