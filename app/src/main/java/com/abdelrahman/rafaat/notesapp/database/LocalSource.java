package com.abdelrahman.rafaat.notesapp.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.abdelrahman.rafaat.notesapp.model.Folder;
import com.abdelrahman.rafaat.notesapp.model.Note;

import java.util.List;

public class LocalSource implements LocalSourceInterface {
    private static LocalSource localSource = null;
    private final NotesDAO dao;
    private final LiveData<List<Note>> notes;
    private final LiveData<List<Folder>> folders;

    public LocalSource(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        dao = db.notesDAO();
        notes = dao.getAllNotes();
        folders = dao.getAllFolders();
    }

    public static LocalSource getInstance(Context context) {
        if (localSource == null) {
            localSource = new LocalSource(context);
        }
        return localSource;
    }

    @Override
    public void insertNote(Note note) {
        new Thread(() -> dao.insertNote(note)).start();
    }

    @Override
    public LiveData<List<Note>> getAllNotes() {
        return notes;
    }

    @Override
    public LiveData<List<Folder>> getAllFolders() {
        return folders;
    }

    @Override
    public void updateNote(Note note) {
        new Thread(() -> dao.updateNote(note)).start();
    }

    @Override
    public void deleteNote(int id) {
        new Thread(() -> dao.deleteNote(id)).start();
    }
}
