package com.abdelrahman.rafaat.notesapp.database;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.abdelrahman.rafaat.notesapp.model.Note;

@Database(entities = Note.class, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabase;
    private final static String databaseName = "NOTES_DATABASE";

    public abstract NotesDAO notesDAO();

    public synchronized static AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, databaseName)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return appDatabase;
    }
}
