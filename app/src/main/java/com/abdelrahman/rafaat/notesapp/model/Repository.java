package com.abdelrahman.rafaat.notesapp.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

import com.abdelrahman.rafaat.notesapp.database.LocalSourceInterface;

import java.util.List;

public class Repository implements RepositoryInterface {
    private static Repository repository = null;
    private final LocalSourceInterface localSource;
    private final SharedPreferences sharedPrefs;
    private final SharedPreferences.Editor editor;

    private Repository(Context context, LocalSourceInterface localSource) {
        this.localSource = localSource;
        this.sharedPrefs = context.getSharedPreferences("LAYOUT_MANGER", Context.MODE_PRIVATE);
        this.editor = sharedPrefs.edit();
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
    public LiveData<List<Folder>> getAllFolders() {
        return localSource.getAllFolders();
    }

    @Override
    public void updateNote(Note note) {
        localSource.updateNote(note);
    }

    @Override
    public void deleteNote(int id) {
        localSource.deleteNote(id);
    }

    @Override
    public boolean getLayoutMangerStyle() {
        return sharedPrefs.getBoolean("IS_LIST", false);
    }

    @Override
    public void setLayoutMangerStyle(boolean isList) {
        editor.putBoolean("IS_LIST", isList);
        editor.apply();
        sharedPrefs.getBoolean("IS_LIST", false);
    }
}