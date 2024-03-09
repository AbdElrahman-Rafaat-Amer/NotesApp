package com.abdelrahman.rafaat.notesapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abdelrahman.rafaat.notesapp.model.Note;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface NotesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    //Pinned Notes
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY isPinned DESC")
    List<Note> getAllNotesPinnedDescending();
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY isPinned ASC")
    List<Note> getAllNotesPinnedAscending();


    //Locked Notes
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY isLocked DESC")
    List<Note> getAllNotesLockedNotesDescending();
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY isLocked ASC")
    List<Note> getAllNotesLockedNotesAscending();


    //TITLE
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY title DESC")
    List<Note> getAllNotesDescendingByTitle();
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY title ASC")
    List<Note> getAllNotesAscendingByTitle();


    //CREATION_DATE
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY date DESC")
    List<Note> getAllNotesDescendingByCreationDate();
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY date ASC")
    List<Note> getAllNotesAscendingByCreationDate();


    //MODIFICATION_DATE
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY modificationDate DESC")
    List<Note> getAllNotesDescendingByModificationDate();
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY modificationDate ASC")
    List<Note> getAllNotesAscendingByModificationDate();

    @Query("SELECT * FROM notes WHERE isArchived = 1")
    List<Note> getArchivedNotes();

    @Update
    Single<Integer> updateNote(Note note);

    @Query("DELETE FROM notes where id = :id")
    Single<Integer> deleteNote(int id);

}
