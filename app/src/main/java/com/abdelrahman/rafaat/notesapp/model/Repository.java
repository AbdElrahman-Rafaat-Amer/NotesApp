package com.abdelrahman.rafaat.notesapp.model;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.abdelrahman.rafaat.notesapp.database.LocalSourceInterface;

import java.util.List;

public class Repository implements RepositoryInterface {

    private static Repository repository = null;
    private Context context;
    private LocalSourceInterface localSource;

    private Repository(Context context, LocalSourceInterface localSource) {
        this.context = context;
        this.localSource = localSource;
    }

    public static Repository getInstance(LocalSourceInterface localSource, Context context) {
        if (repository == null) {
            repository = new Repository(context, localSource);
        }
        return repository;
    }


    @Override
    public void insertNote(Note note) {
        localSource.insertNote(note);
    }

    @Override
    public LiveData<List<Note>> getAllNotes() {
        return localSource.getAllNotes();
    }

    @Override
    public void updateNote(int id, String title, String body) {
        localSource.updateNote(id, title, body);
    }

    @Override
    public void updateNote(Note note) {
        localSource.updateNote(note);
    }

    @Override
    public void deleteNote(int id) {
        localSource.deleteNote(id);
    }
}