package com.abdelrahman.rafaat.notesapp.home.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.RepositoryInterface;

import java.util.List;

public class NoteViewModel extends ViewModel {
    RepositoryInterface repositoryInterface;
    Application application;
    private final MutableLiveData<List<Note>> _notes = new MutableLiveData<>();
    public LiveData<List<Note>> notes = _notes;

    private final MutableLiveData<Boolean> _isList = new MutableLiveData<>();
    public LiveData<Boolean> isList = _isList;

    public NoteViewModel(RepositoryInterface repositoryInterface, Application application) {
        this.repositoryInterface = repositoryInterface;
        this.application = application;
    }

    public void getLayoutMangerStyle() {
        boolean isList = repositoryInterface.getLayoutMangerStyle();
        Log.i("HomeFragment", "getLayoutMangerStyle View: isList---------------------> " + isList);
        _isList.postValue(isList);
    }

    public void setLayoutMangerStyle(boolean isList) {
        repositoryInterface.setLayoutMangerStyle(isList);
    }

    public void getAllNotes() {
        Log.i("HomeFragment", "getAllNotes: ---------------------> ");
        notes = repositoryInterface.getAllNotes();
        Log.i("HomeFragment", "getAllNotes: size---------------------> " + notes);
    }

    public void updateNote(Note note) {
        repositoryInterface.updateNote(note);
    }

    public void deleteNote(int noteID) {
        repositoryInterface.deleteNote(noteID);
    }
}