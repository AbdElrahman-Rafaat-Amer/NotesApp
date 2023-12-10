package com.abdelrahman.rafaat.notesapp.database;

import androidx.lifecycle.LiveData;

import com.abdelrahman.rafaat.notesapp.model.Folder;
import com.abdelrahman.rafaat.notesapp.model.Note;

import java.util.List;

public interface LocalSourceInterface {

    //Notes
    void insertNote(Note note);
    LiveData<List<Note>> getAllNotes();
    LiveData<List<Note>> getAllNotes(int folderID);
    void updateNote(Note note);
    void deleteNote(int id);

    //Folders
    LiveData<List<Folder>> getAllFolders();
    void addFolder(Folder folder);
    void updateFolder(Folder folder);
}
