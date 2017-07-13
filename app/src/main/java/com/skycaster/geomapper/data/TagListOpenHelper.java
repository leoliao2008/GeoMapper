package com.skycaster.geomapper.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.skycaster.geomapper.bean.Tag;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2017/7/13.
 */

public abstract class TagListOpenHelper extends SQLiteOpenHelper {
    public TagListOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public TagListOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }
    public abstract ArrayList<Tag> getTagList();
    public abstract boolean delete(Tag tag);
    public abstract boolean add(Tag tag);
    public abstract boolean add(String name);
    public abstract boolean alter(Tag newTag);
}
