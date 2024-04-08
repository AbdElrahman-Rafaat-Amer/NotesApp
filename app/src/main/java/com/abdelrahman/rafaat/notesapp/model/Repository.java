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
    public List<Note> getFavoritesNotes() {
        return localSource.getFavoritesNotes();
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
    public void savePassword(Passwords password) {
        localSource.savePassword(password);
    }

    @Override
    public List<Passwords> getAllPasswords() {
        return localSource.getAllPasswords();
    }

    @Override
    public void updatePassword(Passwords password) {
        localSource.updatePassword(password);
    }

    @Override
    public void deletePassword(int id) {
        localSource.deletePassword(id);
    }

    @Override
    public boolean isBiometricEnabled() {
        return getBoolean("IS_BIOMETRIC_ENABLED");
    }

    @Override
    public int getTheme() {
        String themeMode = getString("THEME_MODE", "-1");
        return Integer.parseInt(themeMode);
    }

    @Override
    public void savePasswordAndHint(String password, String hint) {
        setString("PASSWORD", password);
        setString("HINT", hint);
    }

    @Override
    public Pair<String, String> getPasswordAndHint() {
        String password = getString("PASSWORD");
        String hint = getString("HINT");
        return new Pair<>(password, hint);
    }

    @Override
    public Pair<SortAction, Boolean> refreshSettings() {
        SortAction sortAction = getSortOrder();
        boolean isListView = getBoolean("IS_LIST", true);
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

    private String getString(String key) {
        return dfaultSharedPreferences.getString(key, "");
    }

    private void setString(String key, String value) {
        SharedPreferences.Editor editor = dfaultSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}