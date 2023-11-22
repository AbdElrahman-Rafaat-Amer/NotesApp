package com.abdelrahman.rafaat.notesapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abdelrahman.rafaat.notesapp.model.Folder;
import com.abdelrahman.rafaat.notesapp.model.Note;

import java.util.List;

@Dao
public interface NotesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    @Query("SELECT * FROM notes ORDER BY isPinned DESC")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM folders ORDER BY isPinned DESC")
    LiveData<List<Folder>> getAllFolders();

    @Update
    void updateNote(Note note);

    @Query("DELETE FROM notes where id = :id")
    void deleteNote(int id);

}
