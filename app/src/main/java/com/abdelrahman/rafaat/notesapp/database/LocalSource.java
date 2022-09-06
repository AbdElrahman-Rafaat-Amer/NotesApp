package com.abdelrahman.rafaat.notesapp.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.abdelrahman.rafaat.notesapp.model.Note;

import java.util.List;

public class LocalSource implements LocalSourceInterface {
    private static LocalSource localSource = null;
    private NotesDAO dao;
    private LiveData<List<Note>> notes;

    public LocalSource(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        dao = db.notesDAO();
        notes = dao.getAllNotes();
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
    public void updateNote(int id, String title, String body) {
        new Thread(() -> dao.updateNote(id, title, body)).start();
    }

    @Override
    public void updateNote(Note note) {
        new Thread(() -> dao.updateNote(note)).start();
    }

    @Override
    public void deleteNote(int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dao.deleteNote(id);
            }
        }).start();
    }
}
