package com.abdelrahman.rafaat.notesapp.addnote.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.RepositoryInterface;

import java.util.List;

public class AddNoteViewModel extends ViewModel {
    RepositoryInterface repositoryInterface;
    Application application;

    public AddNoteViewModel(RepositoryInterface repositoryInterface, Application application) {
        this.repositoryInterface = repositoryInterface;
        this.application = application;
    }

    public void saveNote(Note note) {
        repositoryInterface.insertNote(note);
    }

    public void updateNote(Note note) {
        repositoryInterface.updateNote(note);
    }


}