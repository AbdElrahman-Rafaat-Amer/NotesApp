package com.abdelrahman.rafaat.notesapp.database;

import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.SortAction;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface LocalSourceInterface {

    void insertNote(Note note);

    List<Note> getAllNotes(SortAction sortAction);

    List<Note> getArchivedNotes();

    Single<Integer> updateNote(Note note);

    Single<Integer> deleteNote(int id);
}
