package com.abdelrahman.rafaat.notesapp.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.abdelrahman.rafaat.notesapp.database.LocalSourceInterface;

import java.util.List;

public class Repository implements RepositoryInterface {

    private static Repository repository = null;
    private Context context;
    private LocalSourceInterface localSource;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;


    private Repository(Context context, LocalSourceInterface localSource) {
        this.context = context;
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

    @Override
    public boolean getLayoutMangerStyle() {
        boolean isList = sharedPrefs.getBoolean("IS_LIST", false);
        Log.i("HomeFragment", "getLayoutMangerStyle Repo: isList------------------> " + isList);
        return isList;
    }

    @Override
    public void setLayoutMangerStyle(boolean isList) {
        Log.i("HomeFragment", "setLayoutMangerStyle Repo before: isList-----------------> " + isList);
        editor.putBoolean("IS_LIST", isList);
        editor.apply();

        boolean ss = sharedPrefs.getBoolean("IS_LIST", false);
        Log.i("HomeFragment", "setLayoutMangerStyle Repo after: ss------------------> " + ss);
    }
}