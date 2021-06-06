package com.example.abdo.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;



public class DateBaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "MyDatabase";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "notes";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";

    public DateBaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY , " + KEY_TITLE + " VARCHAR(50), " + KEY_CONTENT + " VARCHAR(50))";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void addNote(NoteDetails noteDetails){

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_TITLE, noteDetails.getNoteTitle());
        contentValues.put(KEY_CONTENT, noteDetails.getNoteContent());

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public ArrayList<NoteDetails> getAllNotesDetails() {
        ArrayList<NoteDetails> contactDetails = new ArrayList<>();
        SQLiteDatabase dp = getReadableDatabase();
        String SELECT = "SELECT * FROM " + TABLE_NAME + "";
        Cursor cursor = dp.rawQuery(SELECT, null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString((cursor.getColumnIndex(KEY_TITLE)));
                String content = cursor.getString(cursor.getColumnIndex(KEY_CONTENT));
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));

                NoteDetails contactDetails1 = new NoteDetails(title, content, id);
                contactDetails.add(contactDetails1);
            } while (cursor.moveToNext());
        }
        return contactDetails;
    }

    public void deleteNote(int id){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, "id=?", new String[] {String.valueOf(id)} );

    }

    public void updateNote(NoteDetails noteDetails){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_TITLE, noteDetails.getNoteTitle());
        contentValues.put(KEY_CONTENT, noteDetails.getNoteContent());

        sqLiteDatabase.update(TABLE_NAME, contentValues, "id=?", new String[]{String.valueOf(noteDetails.getId())});
    }


}
