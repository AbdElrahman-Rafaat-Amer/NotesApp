package com.abdelrahman.rafaat.notesapp.home.viewmodel;

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
    private final MutableLiveData<List<Note>> _notes = new MutableLiveData<>();
    public LiveData<List<Note>> notes = _notes;

    public NoteViewModel(RepositoryInterface repositoryInterface, Application application) {
        this.repositoryInterface = repositoryInterface;
        this.application = application;
    }

    public void getAllNotes() {
        notes = repositoryInterface.getAllNotes();
    }

    public void updateNote(Note note) {
        repositoryInterface.updateNote(note);
    }

    public void deleteNote(int noteID) {
        repositoryInterface.deleteNote(noteID);
    }
}