package com.abdelrahman.rafaat.notesapp.database;

import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.Passwords;
import com.abdelrahman.rafaat.notesapp.model.SortAction;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface LocalSourceInterface {

    void insertNote(Note note);
    List<Note> getAllNotes(SortAction sortAction);
    List<Note> getArchivedNotes();
    List<Note> getFavoritesNotes();
    Single<Integer> updateNote(Note note);
    Single<Integer> deleteNote(int id);
    void savePassword(Passwords password);

    List<Passwords> getAllPasswords();

    Single<Integer> updatePassword(Passwords password);

    Single<Integer> deletePassword(int id) ;
}
