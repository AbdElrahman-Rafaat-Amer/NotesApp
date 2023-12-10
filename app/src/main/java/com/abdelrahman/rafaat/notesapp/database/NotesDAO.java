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

    @Query("SELECT * FROM notes ORDER BY isPinned DESC")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM notes WHERE folderID = :folderID ORDER BY isPinned DESC")
    LiveData<List<Note>> getAllNotes(int folderID);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);
    @Update
    void updateNote(Note note);
    @Query("DELETE FROM notes where id = :id")
    void deleteNote(int id);

    //Folders
    @Query("SELECT * FROM folders ORDER BY isPinned DESC")
    LiveData<List<Folder>> getAllFolders();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addFolder(Folder folder);
    @Update
    void updateFolder(Folder folder);
    @Query("DELETE FROM folders where id = :folderID")
    void deleteFolder(int folderID);
}
