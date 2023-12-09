package com.abdelrahman.rafaat.notesapp.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.database.LocalSourceInterface;

import java.util.ArrayList;
import java.util.List;

public class Repository implements RepositoryInterface {
    private static Repository repository = null;
    private final LocalSourceInterface localSource;
    private final SharedPreferences sharedPreferences;
    private final Context context;

    private Repository(Context context, LocalSourceInterface localSource) {
        this.localSource = localSource;
        this.sharedPreferences = context.getSharedPreferences("LAYOUT_MANGER", Context.MODE_PRIVATE);
        this.context = context;
    }

    public static Repository getInstance(LocalSourceInterface localSource, Context context) {
        if (repository == null) {
            repository = new Repository(context, localSource);
        }
        return repository;
    }


    @Override
    public void insertNote(Note note) {
        updateNotesCount(1);
        localSource.insertNote(note);
    }

    @Override
    public LiveData<List<Note>> getAllNotes() {
        return localSource.getAllNotes();
    }

    @Override
    public void updateNote(Note note) {
        localSource.updateNote(note);
    }

    @Override
    public void deleteNote(int id) {
        updateNotesCount(-1);
        localSource.deleteNote(id);
    }

    @Override
    public boolean getLayoutMangerStyle() {
        return sharedPreferences.getBoolean("IS_LIST", false);
    }

    @Override
    public void setLayoutMangerStyle(boolean isList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IS_LIST", isList);
        editor.apply();
    }


    @Override
    public LiveData<List<Folder>> getAllFolders() {
        LiveData<List<Folder>> savedFolder = localSource.getAllFolders();
        Log.i("AllFoldersFragment", "getAllFolders: Repo savedFolder--------> " + savedFolder.getValue());
        return Transformations.map(savedFolder, originalList -> {
            List<Folder> modifiedList = new ArrayList<>(originalList);

            // Add your two items
            Folder allFolder = new Folder(context.getString(R.string.all));
            allFolder.setChecked(true);
            allFolder.setNumberOfNotes(getNotesCount());
            modifiedList.add(0, allFolder);
            modifiedList.add(new Folder(context.getString(R.string.folder)));
            Log.i("AllFoldersFragment", "getAllFolders: Repo modifiedList--------> " + modifiedList);
            return modifiedList;
        });
    }

    @Override
    public void addFolder(Folder folder) {
        localSource.addFolder(folder);
    }

    private void updateNotesCount(int delta){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int notesCount = getNotesCount() + delta;
        notesCount = Math.max(0, notesCount);
        editor.putInt("NOTES_COUNT", notesCount);
        editor.apply();
    }

    private int getNotesCount(){
        return sharedPreferences.getInt("NOTES_COUNT", 0);
    }
}