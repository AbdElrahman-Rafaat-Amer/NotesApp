package com.abdelrahman.rafaat.notesapp.model;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

public interface RepositoryInterface {

    void insertNote(Note note);

    LiveData<List<Note>> getAllNotes();

    void updateNote(int id, String title, String body);

    void updateNote(Note note);

    void deleteNote(int id);
}
