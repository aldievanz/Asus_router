package com.example.product_bottomnav.ui.product;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user_profile.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PROFILE = "profile";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_CITY = "city";
    private static final String COLUMN_PROVINCE = "province";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_POSTAL_CODE = "postal_code";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PROFILE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_CITY + " TEXT, " +
                COLUMN_PROVINCE + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_POSTAL_CODE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        onCreate(db);
    }

    // Update profile data
    public boolean updateProfile(String name, String address, String city, String province, String phone, String postalCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_ADDRESS, address);
        contentValues.put(COLUMN_CITY, city);
        contentValues.put(COLUMN_PROVINCE, province);
        contentValues.put(COLUMN_PHONE, phone);
        contentValues.put(COLUMN_POSTAL_CODE, postalCode);

        long result = db.update(TABLE_PROFILE, contentValues, null, null);
        return result != -1;
    }

    // Get profile data (optional)
    public Cursor getProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PROFILE, null);
    }
}
