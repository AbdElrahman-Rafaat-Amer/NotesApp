package com.abdelrahman.rafaat.notesapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.abdelrahman.rafaat.notesapp.database.LocalSource;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.Repository;
import com.abdelrahman.rafaat.notesapp.model.RepositoryInterface;

import java.util.ArrayList;
import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private final RepositoryInterface repositoryInterface;
    public LiveData<List<Note>> notes;
    private MutableLiveData<List<String>> _files = new MutableLiveData<List<String>>();
    public LiveData<List<String>> files = _files;
    private final MutableLiveData<Boolean> _isList = new MutableLiveData<>();
    public LiveData<Boolean> isList = _isList;
    private Note currentNote;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        this.repositoryInterface = Repository.getInstance(
                LocalSource.getInstance(application.getApplicationContext()), application.getApplicationContext());
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
        List<String> files = new ArrayList<String>();
        files.add("All");
        _files.postValue(files);
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

    public void setCurrentNote(Note note) {
        this.currentNote = note;
    }

    public Note getCurrentNote() {
        return currentNote;
    }
}