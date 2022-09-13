package com.abdelrahman.rafaat.notesapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.abdelrahman.rafaat.notesapp.model.RepositoryInterface;

public class NotesViewModelFactory implements ViewModelProvider.Factory {
    RepositoryInterface repositoryInterface;
    Application application;

    public NotesViewModelFactory(RepositoryInterface repositoryInterface, Application application) {
        this.repositoryInterface = repositoryInterface;
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NoteViewModel(repositoryInterface, application);
    }
}
