package com.abdelrahman.rafaat.notesapp.model;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface RepositoryInterface {

    void insertNote(Note note);

    LiveData<List<Note>> getAllNotes();

    void updateNote(int id, String title, String body);

    void updateNote(Note note);

    void deleteNote(int id);

    boolean getLayoutMangerStyle();

    void setLayoutMangerStyle(boolean isList);
}
