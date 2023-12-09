package com.abdelrahman.rafaat.notesapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.database.LocalSource;
import com.abdelrahman.rafaat.notesapp.model.Folder;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.Repository;
import com.abdelrahman.rafaat.notesapp.model.RepositoryInterface;

import java.util.ArrayList;
import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private final RepositoryInterface repositoryInterface;
    public LiveData<List<Note>> notes;

    private final MutableLiveData<List<Folder>> _folders = new MutableLiveData<>();
    public LiveData<List<Folder>> folders = _folders;
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
    }

    public void getAllFolders() {
//        LiveData<List<Folder>> savedFolder = repositoryInterface.getAllFolders();
//        List<Folder> currentFolders = savedFolder.getValue();
//        if (currentFolders == null){
//            currentFolders = new ArrayList<>();
//        }
//        Folder allFolder = new Folder(getApplication().getString(R.string.all));
//        allFolder.setChecked(true);
//        currentFolders.add(0, allFolder);
//        currentFolders.add(new Folder(getApplication().getString(R.string.folder)));
//        _folders.postValue(currentFolders);
        folders = repositoryInterface.getAllFolders();
//        _folders.postValue(folders);
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