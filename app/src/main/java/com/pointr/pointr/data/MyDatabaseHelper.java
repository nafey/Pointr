package com.pointr.pointr.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper{
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

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

    public void addPointr(String num, float lat, float lng) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NUM_COLUMN_NAME, num);
        values.put(LAT_COLUMN_NAME, lat);
        values.put(LNG_COLUMN_NAME, lng);

        // Inserting Row
        db.insert(POINTR_TABLE_NAME, null, values);
        db.close(); // Closing database connection
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
