package com.abdelrahman.rafaat.notesapp.database;

import androidx.lifecycle.LiveData;

import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.SortAction;

import java.util.List;

public interface LocalSourceInterface {

    void insertNote(Note note);
    LiveData<List<Note>> getAllNotes();
    List<Note> getAllNotes(SortAction sortAction);
    List<Note> getArchivedNotes();
    void updateNote(Note note);
    void deleteNote(int id);
}
