package com.abdelrahman.rafaat.notesapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.abdelrahman.rafaat.notesapp.model.Note;

@Database(entities = Note.class, version = 6)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabase;
    private static final String DATABASE_NAME = "NOTES_DATABASE";

    public abstract NotesDAO notesDAO();

    public static synchronized AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return appDatabase;
    }
}
