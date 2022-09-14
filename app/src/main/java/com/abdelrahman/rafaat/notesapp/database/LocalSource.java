package com.abdelrahman.rafaat.notesapp.database;

import android.content.Context;
import android.util.Log;

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
        //      Log.i("LocalSource", "getAllNotes: notes--------------------------> " + notes.getValue().size());
        //      Log.i("LocalSource", "getAllNotes: before pinnedNotes------------------> " + pinnedNotes.getValue().size());
        //      Log.i("LocalSource", "getAllNotes: before nonPinnedNotes------------------> " + nonPinnedNotes.getValue().size());
        //        boolean isAdded = pinnedNotes.getValue().addAll(nonPinnedNotes.getValue());
        //      Log.i("LocalSource", "getAllNotes: isAdded----------------------------> " + isAdded);
        //       Log.i("LocalSource", "getAllNotes: after pinnedNotes------------------> " + pinnedNotes.getValue().size());
        //       Log.i("LocalSource", "getAllNotes: after nonPinnedNotes------------------> " + nonPinnedNotes.getValue().size());
        return notes;
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

    @Override
    public void lockNote(int noteID, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dao.lockNote(noteID, password);
            }
        }).start();
    }
}
