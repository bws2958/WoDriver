package com.wodriver.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 2016-11-29.
 */

public class DBManager extends SQLiteOpenHelper {
    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE USER_INFO(_id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, passwork TEXT);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
