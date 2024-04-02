package com.abdelrahman.rafaat.notesapp.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.abdelrahman.rafaat.notesapp.model.Note;

@Database(entities = Note.class, version = 7)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabase;
    private static final String DATABASE_NAME = "NOTES_DATABASE";

    public abstract NotesDAO notesDAO();


    public static synchronized AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_4_6)
                    .addMigrations(MIGRATION_6_7)
                    .build();
        }
        return appDatabase;
    }

   private static final Migration MIGRATION_4_6 = new Migration(4, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create a new table with the updated schema
            database.execSQL("CREATE TABLE IF NOT EXISTS `notes_new` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`title` TEXT, " +
                    "`body` TEXT, " +
                    "`creationDate` TEXT, " +
                    "`modificationDate` INTEGER NOT NULL DEFAULT -1, " +
                    "`color` INTEGER NOT NULL DEFAULT 0, " +
                    "`isPinned` INTEGER NOT NULL DEFAULT 0, " +
                    "`password` TEXT, " +
                    "`isLocked` INTEGER NOT NULL DEFAULT 0, " +
                    "`textSize` INTEGER NOT NULL DEFAULT 0, " +
                    "`textAlignment` INTEGER NOT NULL DEFAULT 0, " +
                    "`isArchived` INTEGER NOT NULL DEFAULT 0)");

            // Copy data from the old table to the new one
            database.execSQL("INSERT INTO notes_new (id, title, body, creationDate, color, isPinned, password, textSize, textAlignment, isArchived) " +
                    "SELECT id, title, body, date, color, isPinned, password, textSize, textAlignment, isArchived FROM notes");

            // Drop the old table
            database.execSQL("DROP TABLE IF EXISTS `notes`");

            // Rename the new table to match the old one
            database.execSQL("ALTER TABLE `notes_new` RENAME TO `notes`");
        }
    };

    private static final Migration MIGRATION_6_7 = new Migration(6,7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE notes ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0");
            database.execSQL("UPDATE notes SET isFavorite = 0");
        }
    };
}
