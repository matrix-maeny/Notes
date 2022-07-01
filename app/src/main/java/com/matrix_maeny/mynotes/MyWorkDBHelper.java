package com.matrix_maeny.mynotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;

// one db helper for all events

public class MyWorkDBHelper extends SQLiteOpenHelper {

    private int databaseNumber; // dataBase number
    private String dataBaseName; // database name

    public MyWorkDBHelper(@Nullable Context context, @Nullable String name, int databaseNumber) {
        super(context, name, null, 1);
        this.databaseNumber = databaseNumber;
        this.dataBaseName = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        switch (databaseNumber) {
            case 1:
                db.execSQL("Create Table SpeechSettings(name TEXT primary key, value INT, voice TEXT, voiceCode TEXT, country TEXT)");
                break;
            case 2:
                db.execSQL("Create Table NotesData(heading TEXT primary key, content TEXT)");
                break;

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (databaseNumber) {
            case 1:
                db.execSQL("drop Table if exists SpeechSettings");
                break;
            case 2:
                db.execSQL("drop Table if exists NotesData");
                break;

        }
    }

    // speech settings section =================================
    public boolean insertSpeechData(String name, float value, String voice, String voiceCode, String country) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("value", value);
        cv.put("voice", voice);
        cv.put("voiceCode", voiceCode);
        cv.put("country", country);

        long result = db.insert("SpeechSettings", null, cv);

        return result != -1;
    }

    public boolean updateSpeechData(String name, int value, String voice, String voiceCode, String country) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("value", value);
        cv.put("voice", voice);
        cv.put("voiceCode", voiceCode);
        cv.put("country", country);

        Cursor cursor = db.rawQuery("Select * from SpeechSettings where name = ?", new String[]{name});

        if (cursor.getCount() > 0) {
            long result = db.update("SpeechSettings", cv, "name=?", new String[]{name});

            return result != -1;
        } else {
            return false;
        }
    }


    public Cursor getData() {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("Select * from SpeechSettings", null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }

    }


    // notes session ===============================================================

    public boolean insertNoteData(String heading, String content) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("heading", heading);
        cv.put("content", content);

        Cursor cursor = db.rawQuery("Select * from NotesData", null);

        long result = db.insert("NotesData", null, cv);
        return result != -1;


    }

    public boolean updateNoteDate(String heading, String content) { // to update the note data


        ContentValues cv = new ContentValues();


        cv.put("content", content);
        boolean updated = false;


        if (deleteNotesData(heading)) {
            updated = insertNoteData(heading, content);
        }


        return updated;


    } // complete

    public Cursor getNotesData() { // for getting the notes data

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("Select * from NotesData", null);
        return cursor;

    }

    public boolean deleteNotesData(String name) {

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.rawQuery("Select * from NotesData where heading = ?", new String[]{name});

        if (cursor.getCount() > 0) {
            long result = db.delete("NotesData", "heading = ?", new String[]{name});

            return result != -1;
        } else {
            return false;
        }
    }

    public boolean deleteAllNotes() {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete("NotesData", "", null);


        return result != -1;
    }


    // notes ended.................................


}
