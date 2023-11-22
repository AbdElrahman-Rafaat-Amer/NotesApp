package com.abdelrahman.rafaat.notesapp.database;

import androidx.lifecycle.LiveData;

import com.abdelrahman.rafaat.notesapp.model.Folder;
import com.abdelrahman.rafaat.notesapp.model.Note;

import java.util.List;

public interface LocalSourceInterface {

    void insertNote(Note note);

    LiveData<List<Note>> getAllNotes();
    LiveData<List<Folder>> getAllFolders();

    void updateNote(Note note);

    void deleteNote(int id);
}
