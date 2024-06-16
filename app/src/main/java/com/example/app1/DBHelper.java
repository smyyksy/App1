package com.example.app1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AppDB.db";
    private static final int DATABASE_VERSION = 2; // Veritabanı sürümünü artırdık

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabloların oluşturulması için SQL sorgularını buraya yazın
        String createUserTable = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE, " +
                "password TEXT)";
        db.execSQL(createUserTable);

        String createExperienceTable = "CREATE TABLE experiences (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "experience TEXT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(createExperienceTable);

        String createMedicationTable = "CREATE TABLE medications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "name TEXT, " +
                "time TEXT, " +
                "taken INTEGER DEFAULT 0, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(createMedicationTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eski tabloları düşürüp yeniden oluşturuyoruz
        db.execSQL("DROP TABLE IF EXISTS experiences");
        db.execSQL("DROP TABLE IF EXISTS medications");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public long addExperience(int userId, String experience) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("experience", experience);

        long newRowId = db.insert("experiences", null, values);

        db.close();

        return newRowId;
    }

    public Cursor getExperiences(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {"experience", "timestamp"};
        String selection = "user_id = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        String sortOrder = "timestamp DESC";

        return db.query("experiences", projection, selection, selectionArgs, null, null, sortOrder);
    }

    public Cursor getAllExperiences() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {"id", "user_id", "experience", "timestamp"};
        String sortOrder = "timestamp DESC";

        return db.query("experiences", projection, null, null, null, null, sortOrder);
    }

    public String getUsername(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {"username"};
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query("users", projection, selection, selectionArgs, null, null, null);
        String username = "";

        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
        }

        cursor.close();
        return username;
    }

    public long addMedication(int userId, String name, String time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("name", name);
        values.put("time", time);

        long newRowId = db.insert("medications", null, values);

        db.close();

        return newRowId;
    }

    public Cursor getMedications(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {"id", "name", "time", "taken"};
        String selection = "user_id = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        String sortOrder = "time ASC";

        return db.query("medications", projection, selection, selectionArgs, null, null, sortOrder);
    }

    public void updateMedicationStatus(int id, boolean taken) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("taken", taken ? 1 : 0);

        db.update("medications", values, "id = ?", new String[]{String.valueOf(id)});

        db.close();
    }

    public boolean deleteMedication(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete("medications", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return deletedRows > 0;
    }
}
