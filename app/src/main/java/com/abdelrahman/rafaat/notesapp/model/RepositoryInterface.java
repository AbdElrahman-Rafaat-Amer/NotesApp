package com.abdelrahman.rafaat.notesapp.model;

import android.util.Pair;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface RepositoryInterface {
    void insertNote(Note note);

    List<Note> getAllNotes(SortAction sortAction);

    List<Note> getArchivedNotes();

    List<Note> getFavoritesNotes();

    Single<Integer> updateNote(Note note);

    Single<Integer> deleteNote(int id);

    boolean isBiometricEnabled();

    int getTheme();

    void savePassword(String password, String hint);

    Pair<String, String> getPasswordAndHint();
    Pair<SortAction, Boolean> refreshSettings();
}
