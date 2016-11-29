package com.wodriver.util;

import android.content.Context;
import android.database.Cursor;
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
        db.execSQL("CREATE TABLE USER_INFO(_id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, password TEXT, " +
                "given_name TEXT, email TEXT, phone TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public String PrintData() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from USER_INFO", null);
        while(cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " : userName "
                    + cursor.getString(1)
                    + ", password = "
                    + cursor.getString(2)
                    + ", given_name = "
                    + cursor.getString(3)
                    + ", email = "
                    + cursor.getString(4)
                    + ", phone = "
                    + cursor.getString(5)
                    + "\n";
        }

        return str;
    }
}

