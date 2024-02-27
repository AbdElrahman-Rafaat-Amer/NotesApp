package com.abdelrahman.rafaat.notesapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abdelrahman.rafaat.notesapp.model.Note;

import java.util.List;

@Dao
public interface NotesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY isPinned DESC")
    LiveData<List<Note>> getAllNotes();


    //Pinned Notes
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY isPinned DESC")
    List<Note> getAllPinnedNotesDescending();

    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY isPinned ASC")
    List<Note> getAllPinnedNotesAscending();


    //Locked Notes
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY isLocked DESC")
    List<Note> getAllLockedNotesDescending();

    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY isLocked ASC")
    List<Note> getAllLockedNotesAscending();


    //TITLE
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY title DESC")
    List<Note> getAllDescendingByTitle();

    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY title ASC")
    List<Note> getAllNotesAscendingByTitle();


    //CREATION_DATE
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY date DESC")
    List<Note> getAllNotesDescendingByCreationData();

    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY date ASC")
    List<Note> getAllNotesAscendingByCreationData();


    //MODIFICATION_DATE
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY modificationDate DESC")
    List<Note> getAllDescendingByModificationDate();

    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY modificationDate ASC")
    List<Note> getAllNotesAscendingByModificationDate();


    @Query("SELECT * FROM notes WHERE isArchived = 1")
    List<Note> getArchivedNotes();

    @Update
    void updateNote(Note note);

    @Query("DELETE FROM notes where id = :id")
    void deleteNote(int id);

}
