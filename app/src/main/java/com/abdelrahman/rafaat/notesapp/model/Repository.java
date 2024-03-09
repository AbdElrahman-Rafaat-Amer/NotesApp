package com.abdelrahman.rafaat.notesapp.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.preference.PreferenceManager;

import com.abdelrahman.rafaat.notesapp.database.LocalSourceInterface;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class Repository implements RepositoryInterface {
    private static Repository repository = null;
    private final LocalSourceInterface localSource;
    private final SharedPreferences dfaultSharedPreferences;

    private Repository(Context context, LocalSourceInterface localSource) {
        this.localSource = localSource;
        this.dfaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static RepositoryInterface getInstance(LocalSourceInterface localSource, Context context) {
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
    public List<Note> getAllNotes(SortAction sortAction) {
        return localSource.getAllNotes(sortAction);
    }

    @Override
    public List<Note> getArchivedNotes() {
        return localSource.getArchivedNotes();
    }

    @Override
    public Single<Integer> updateNote(Note note) {
        return localSource.updateNote(note);
    }

    @Override
    public Single<Integer> deleteNote(int id) {
        return localSource.deleteNote(id);
    }

    @Override
    public boolean isBiometricEnabled() {
        return getBoolean("IS_BIOMETRIC_ENABLED");
    }

    @Override
    public Pair<SortAction, Boolean> refreshSettings() {
        SortAction sortAction = getSortOrder();
        boolean isListView = getBoolean("IS_LIST");
        return new Pair<>(sortAction, isListView);
    }

    private SortAction getSortOrder() {
        SortAction sortAction = new SortAction();
        boolean sortOrder = getBoolean("SORT_ORDER", true);
        String sortType = getString("SORT_KEY", SortType.PINNED_NOTES.toString());
        sortAction.setSortOrder(sortOrder);
        sortAction.setSortType(SortType.valueOf(sortType));
        return sortAction;
    }

    private boolean getBoolean(String key) {
        return dfaultSharedPreferences.getBoolean(key, false);
    }

    private boolean getBoolean(String key, boolean defValue) {
        return dfaultSharedPreferences.getBoolean(key, defValue);
    }

    private String getString(String key, String defValue) {
        return dfaultSharedPreferences.getString(key, defValue);
    }
}