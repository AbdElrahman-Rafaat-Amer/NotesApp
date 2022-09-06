package com.abdelrahman.rafaat.notesapp.addnote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.abdelrahman.rafaat.notesapp.model.RepositoryInterface;

public class AddNotesViewModelFactory implements ViewModelProvider.Factory {
    RepositoryInterface repositoryInterface;
    Application application;

    public AddNotesViewModelFactory(RepositoryInterface repositoryInterface, Application application) {
        this.repositoryInterface = repositoryInterface;
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddNoteViewModel(repositoryInterface, application);
    }
}
