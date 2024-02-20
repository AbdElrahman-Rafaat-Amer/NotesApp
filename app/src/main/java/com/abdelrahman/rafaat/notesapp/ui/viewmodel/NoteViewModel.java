package com.abdelrahman.rafaat.notesapp.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.abdelrahman.rafaat.notesapp.database.LocalSource;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.Repository;
import com.abdelrahman.rafaat.notesapp.model.RepositoryInterface;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NoteViewModel extends AndroidViewModel {
    private final RepositoryInterface repositoryInterface;
    public LiveData<List<Note>> notes;
    List<Note> lisarchivedNotes;
    public MutableLiveData<List<Note>> _archivedNotes = new MutableLiveData<>();
    public LiveData<List<Note>> archivedNotes = _archivedNotes;
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
    public void getArchivedNotes() {
        Observable<LiveData<List<Note>>> observable = Observable.create(emitter -> {
            // Simulate data generation or retrieval
            lisarchivedNotes = repositoryInterface.getArchivedNotes();
            Log.i("ARCHIVED_NOTES", "getArchivedNotes: " + archivedNotes);
            // Emit the items to the subscribers
            emitter.onNext(notes);

            // Complete the observable (optional)
            emitter.onComplete();
        });

        Disposable disposable = observable
                .subscribeOn(Schedulers.io())  // Specify the background thread for data generation
                .observeOn(AndroidSchedulers.mainThread())  // Specify the main thread for handling results
                .subscribe(
                        items -> {
                            // Handle emitted items on the main thread
                            // items contains the list of items
                            _archivedNotes.setValue(lisarchivedNotes);
                            Log.d("ARCHIVED_NOTES", "Received items: " + items);
                        },
                        throwable -> {
                            // Handle errors
                            Log.e("ARCHIVED_NOTES", "Received items Error: " + throwable.getMessage());
                        }
                );
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