package com.abdelrahman.rafaat.notesapp.model;

import android.util.Pair;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface RepositoryInterface {
    void insertNote(Note note);

    LiveData<List<Note>> getAllNotes();
    List<Note> getAllNotes(SortAction sortAction);

    List<Note> getArchivedNotes();

    void updateNote(Note note);

    void deleteNote(int id);

    boolean isBiometricEnabled();

    Pair<SortAction, Boolean> refreshSettings();
}
