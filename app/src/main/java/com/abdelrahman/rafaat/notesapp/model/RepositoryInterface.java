package com.abdelrahman.rafaat.notesapp.model;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface RepositoryInterface {

    void insertNote(Note note);

    LiveData<List<Note>> getAllNotes();

    void updateNote(Note note);

    void deleteNote(int id);

    void lockNote(int noteID, String password);

    boolean getLayoutMangerStyle();

    void setLayoutMangerStyle(boolean isList);
}
