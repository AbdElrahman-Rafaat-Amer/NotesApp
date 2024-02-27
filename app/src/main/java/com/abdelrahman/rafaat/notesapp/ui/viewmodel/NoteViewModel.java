package com.abdelrahman.rafaat.notesapp.ui.viewmodel;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.abdelrahman.rafaat.notesapp.database.LocalSource;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.Repository;
import com.abdelrahman.rafaat.notesapp.model.RepositoryInterface;
import com.abdelrahman.rafaat.notesapp.model.SortAction;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NoteViewModel extends AndroidViewModel {
    private final RepositoryInterface repositoryInterface;
    List<Note> _notes;
    private final MutableLiveData<List<Note>> notes = new MutableLiveData<>();

    List<Note> listArchivedNotes;
    public MutableLiveData<List<Note>> _archivedNotes = new MutableLiveData<>();
    public LiveData<List<Note>> archivedNotes = _archivedNotes;
    private Note currentNote;

    private SortAction _sortAction = new SortAction();
    private MutableLiveData<Boolean> _isListView = new MutableLiveData<>();
    public LiveData<Boolean> isListView = _isListView;


    private Disposable notesDisposable;
    private Disposable archivedNotesDisposable;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        this.repositoryInterface = Repository.getInstance(
                LocalSource.getInstance(application.getApplicationContext()), application.getApplicationContext());
        refreshSettings();
    }

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

    public void getAllNotes() {
        notesDisposable = Observable.create(emitter -> {
                    // Simulate data generation or retrieval
                    _notes = repositoryInterface.getAllNotes(_sortAction);
                    // Emit the items to the subscribers
                    emitter.onNext(_notes);

                    // Complete the observable (optional)
                    emitter.onComplete();
                }).subscribeOn(Schedulers.io())  // Specify the background thread for data generation
                .observeOn(AndroidSchedulers.mainThread())  // Specify the main thread for handling results
                .subscribe(
                        items -> {
                            // Handle emitted items on the main thread
                            // items contains the list of items
                            notes.setValue(_notes);
                        },
                        throwable -> {
                            Log.e("ARCHIVED_NOTES", "Received items Error: " + throwable.getMessage());
                        }
                );
    }

    public void getArchivedNotes() {
        archivedNotesDisposable = Observable.create(emitter -> {
                    // Simulate data generation or retrieval
                    listArchivedNotes = repositoryInterface.getArchivedNotes();
                    // Emit the items to the subscribers
                    emitter.onNext(listArchivedNotes);

                    // Complete the observable (optional)
                    emitter.onComplete();
                }).subscribeOn(Schedulers.io())  // Specify the background thread for data generation
                .observeOn(AndroidSchedulers.mainThread())  // Specify the main thread for handling results
                .subscribe(
                        items -> {
                            // Handle emitted items on the main thread
                            // items contains the list of items
                            _archivedNotes.setValue(listArchivedNotes);
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

    public void refreshSettings() {
        Pair<SortAction, Boolean> settings = repositoryInterface.refreshSettings();
        SortAction sortAction = settings.first;
        boolean isListView = settings.second;
        if (!sortAction.equals(_sortAction)) {
            _sortAction = sortAction;
            getAllNotes();
        }

        if (!Objects.equals(_isListView.getValue(), isListView)) {
            _isListView.setValue(isListView);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (notesDisposable != null) {
            notesDisposable.dispose();
        }
        if (archivedNotesDisposable != null) {
            archivedNotesDisposable.dispose();
        }
    }
}