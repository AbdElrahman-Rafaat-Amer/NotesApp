package com.abdelrahman.rafaat.notesapp.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.RepositoryInterface;

import java.util.List;

public class NoteViewModel extends ViewModel {
    RepositoryInterface repositoryInterface;
    Application application;
    public LiveData<List<Note>> notes;

    private final MutableLiveData<Boolean> _isList = new MutableLiveData<>();
    public LiveData<Boolean> isList = _isList;

    public NoteViewModel(RepositoryInterface repositoryInterface, Application application) {
        this.repositoryInterface = repositoryInterface;
        this.application = application;
    }

    public void getLayoutMangerStyle() {
        boolean isList = repositoryInterface.getLayoutMangerStyle();
        _isList.postValue(isList);
    }

    public void setLayoutMangerStyle(boolean isList) {
        repositoryInterface.setLayoutMangerStyle(isList);
    }

    public void getAllNotes() {
        notes = repositoryInterface.getAllNotes();
    }

    public void saveNote(Note note) {
        repositoryInterface.insertNote(note);
    }

    public void updateNote(Note note) {
        repositoryInterface.updateNote(note);
    }

    public void deleteNote(int noteID) {
        repositoryInterface.deleteNote(noteID);
    }

    public void lockNote(int noteID, String password) {
        repositoryInterface.lockNote(noteID, password);
    }
}