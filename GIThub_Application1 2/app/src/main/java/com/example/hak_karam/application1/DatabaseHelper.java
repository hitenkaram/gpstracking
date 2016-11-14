package com.example.hak_karam.application1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by hak_karam on 25/04/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Navigation.db";
    public static final String TABLE_NAME = "navigate_table";
    public static final String COL_1_1 = "DEVICE_ID";
    public static final String COL_1 = "LOCATIONID";
    public static final String COL_2 = "LONGITUDE";
    public static final String COL_3 = "LATITUDE";
    public static final String COL_4 = "SPEED";
    public static final String COL_5 = "TIMESTAMP";
    public static final String COL_6 = "xAxisValue";
    public static final String COL_7 = "yAxisValue";
    public static final String COL_8 = "zAxisValue";

    SQLiteDatabase db;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    public void open() throws SQLException {
        db = this.getWritableDatabase();
        //return true;
    }
//    public void close(){
//        this.close();
//        //return true;
//    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (LOCATIONID INTEGER PRIMARY KEY AUTOINCREMENT,DEVICE_ID TEXT, LONGITUDE TEXT,LATITUDE TEXT,SPEED TEXT,TIMESTAMP TEXT,xAxisValue TEXT,yAxisValue TEXT,zAxisValue TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }

    public boolean insertNavData(String DEVID,String LONG,String LAT,String SPE,String TS,String X,String Y,String Z) {
        //showToast("DB Class ");
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1_1,DEVID);
        contentValues.put(COL_2,LONG);
        contentValues.put(COL_3,LAT);
        contentValues.put(COL_4,SPE);
        contentValues.put(COL_5,TS);
        contentValues.put(COL_6,X);
        contentValues.put(COL_7,Y);
        contentValues.put(COL_8,Z);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        //db.close();
        if(result == -1)
        {
            return false;

        }
        else
        {
            return true;
        }
    }


    public Cursor getAllNavRows(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=db.query(TABLE_NAME, new String[]{COL_1_1,COL_2,COL_3,COL_4,COL_5,COL_6,COL_7,COL_8}, null,null, null, null, null);
        return cursor;
    }

    public void deleteRows(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
    }

}
