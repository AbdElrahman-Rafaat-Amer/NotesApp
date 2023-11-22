package com.abdelrahman.rafaat.notesapp.database;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.abdelrahman.rafaat.notesapp.model.Folder;
import com.abdelrahman.rafaat.notesapp.model.Note;

@Database(entities = {Note.class, Folder.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabase;
    private final static String databaseName = "NOTES_DATABASE";

    public abstract NotesDAO notesDAO();

    public synchronized static AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, databaseName)
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return appDatabase;
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new 'folders' table
            database.execSQL("CREATE TABLE IF NOT EXISTS `folders` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT, " +
                    "`password` TEXT, " +
                    "`isPinned` INTEGER NOT NULL DEFAULT 0, " +
                    "`isChecked` INTEGER NOT NULL DEFAULT 0, " +
                    "`numberOfNotes` INTEGER NOT NULL DEFAULT 0)");

            // You can copy data from the 'notes' table to the new 'folders' table if needed
            // Example: database.execSQL("INSERT INTO `folders` SELECT * FROM `notes`");
        }
    };
}
