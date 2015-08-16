package com.pointr.pointr.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;
import android.util.Log;

import com.pointr.pointr.util.MyHelper;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper{
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "MyDB";

    // Contacts table name
    private static final String POINTR_TABLE_NAME = "pointr";

    // Contacts Table Columns names
    private static final String NUM_COLUMN_NAME = "num";
    private static final String LAT_COLUMN_NAME = "lat";
    private static final String LNG_COLUMN_NAME = "lng";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + POINTR_TABLE_NAME + "("
                + NUM_COLUMN_NAME + " TEXT PRIMARY KEY," + LAT_COLUMN_NAME + " REAL,"
                + LNG_COLUMN_NAME + " REAL" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + POINTR_TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    // Getting single pointr
    public Pointr getPointr(String id) {
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(POINTR_TABLE_NAME, new String[] { NUM_COLUMN_NAME,
                        LAT_COLUMN_NAME, LNG_COLUMN_NAME}, NUM_COLUMN_NAME + "=?",
                new String[] { id }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            Pointr pointr = new Pointr(cursor.getString(0), cursor.getFloat(1), cursor.getFloat(2));
            return pointr;
        } else {
            return null;
        }
    }

    public void addPointr(String num, float lat, float lng) {
        if (!MyHelper.isCorrectNumberFormat(num)) return;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NUM_COLUMN_NAME, num);
        values.put(LAT_COLUMN_NAME, lat);
        values.put(LNG_COLUMN_NAME, lng);

        if (this.getPointr(num) == null) {
            // Inserting Row
            db.insert(POINTR_TABLE_NAME, null, values);
        } else {
            // Updating Row
            db.update(POINTR_TABLE_NAME, values, NUM_COLUMN_NAME + "=?", new String[] { num });

            Log.d("TAG","updated");
        }
        db.close(); // Closing database connection
    }



    // Getting All Pointrs
    public List<Pointr> getAllPointrs() {
        List<Pointr> pointrList = new ArrayList<Pointr>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + POINTR_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Pointr pointr = new Pointr(cursor.getString(0), cursor.getFloat(1), cursor.getFloat(2));

                // Adding contact to list
                pointrList.add(pointr);
            } while (cursor.moveToNext());
        }

        // return contact list
        return pointrList;
    }

    // Updating single contact
    public int updatePointr (Pointr pointr) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NUM_COLUMN_NAME, pointr.getNum());
        values.put(LAT_COLUMN_NAME, pointr.getLoc().latitude);
        values.put(LNG_COLUMN_NAME, pointr.getLoc().longitude);

        // updating row
        return db.update(POINTR_TABLE_NAME, values, NUM_COLUMN_NAME + " = ?",
                new String[] { String.valueOf(pointr.getNum()) });
    }


    // Deleting single contact
    public void deletePointr(Pointr pointr) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(POINTR_TABLE_NAME, NUM_COLUMN_NAME + " = ?",
                new String[]{String.valueOf(pointr.getNum())});
        db.close();
    }



    public int getPointrCount() {
        int ret;

        String countQuery = "SELECT  * FROM " + POINTR_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        ret = cursor.getCount();

        cursor.close();
        return ret;
    }



}
