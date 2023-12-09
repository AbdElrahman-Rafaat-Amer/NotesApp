package com.abdelrahman.rafaat.notesapp.model;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface RepositoryInterface {

    //Notes
    void insertNote(Note note);
    LiveData<List<Note>> getAllNotes();
    void updateNote(Note note);
    void deleteNote(int id);
    boolean getLayoutMangerStyle();
    void setLayoutMangerStyle(boolean isList);

    //Folders
    LiveData<List<Folder>> getAllFolders();
    void addFolder(Folder folder);
}
