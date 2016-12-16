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

        db.execSQL("CREATE TABLE HR_DATA(_id INTEGER PRIMARY KEY AUTOINCREMENT, hr REAL, x REAL, y REAL, z REAL);");

        db.execSQL("CREATE TABLE LATLON(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat REAL, lon REAL);");
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

        Cursor cursor = db.rawQuery("select * from HR_DATA", null);
        while(cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " : hr "
                    + cursor.getDouble(1)
                    + ", x = "
                    + cursor.getDouble(2)
                    + ", y = "
                    + cursor.getDouble(3)
                    + ", z = "
                    + cursor.getDouble(4)
                    + "\n";
        }

        return str;
    }

    public String Print_hr_Data() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from HR_DATA", null);
        while(cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " : hr "
                    + cursor.getString(1)
                    + "\n";
        }

        return str;
    }

    public String print_lanlon(){
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from LATLON", null);
        while(cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " : lat "
                    + cursor.getString(1)
                    + " : lon "
                    + cursor.getString(2)
                    + "\n";
        }

        return str;
    }
}

