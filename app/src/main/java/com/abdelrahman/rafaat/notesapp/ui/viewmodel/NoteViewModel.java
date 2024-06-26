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

    private List<Note> _notes;
    private final MutableLiveData<List<Note>> notes = new MutableLiveData<>();

    private List<Note> listArchivedNotes;
    private final MutableLiveData<List<Note>> _archivedNotes = new MutableLiveData<>();
    public LiveData<List<Note>> archivedNotes = _archivedNotes;
    private Note currentNote;

    private SortAction _sortAction = new SortAction();
    private final MutableLiveData<Boolean> _isListView = new MutableLiveData<>();
    public LiveData<Boolean> isListView = _isListView;

    private Disposable notesDisposable;
    private Disposable archivedNotesDisposable;
    private Disposable updateNotesDisposable;
    private Disposable deleteNotesDisposable;

    private boolean isArchivedNotes = false;
    private static final String TAG = "Note_ViewModel_TAG";

    public NoteViewModel(@NonNull Application application) {
        super(application);
        this.repositoryInterface = Repository.getInstance(
                LocalSource.getInstance(application.getApplicationContext()), application.getApplicationContext());
        refreshSettings();
    }

    public int getTheme() {
        return repositoryInterface.getTheme();
    }

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

    public void getAllNotes() {
        isArchivedNotes = false;
        notesDisposable = Observable.create(emitter -> {
                    // Simulate data generation or retrieval
                    _notes = repositoryInterface.getAllNotes(_sortAction);
                    // Emit the items to the subscribers
                    emitter.onNext(_notes);
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())  // Specify the background thread for data generation
                .observeOn(AndroidSchedulers.mainThread())  // Specify the main thread for handling results
                .subscribe(
                        items ->
                                notes.setValue(_notes),
                        throwable ->
                                Log.e(TAG, "Received items Error: " + throwable.getMessage())
                );
    }

    public void getArchivedNotes() {
        isArchivedNotes = true;
        archivedNotesDisposable = Observable.create(emitter -> {
                    // Simulate data generation or retrieval
                    listArchivedNotes = repositoryInterface.getArchivedNotes();
                    // Emit the items to the subscribers
                    emitter.onNext(listArchivedNotes);

                    // Complete the observable (optional)
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())  // Specify the background thread for data generation
                .observeOn(AndroidSchedulers.mainThread())  // Specify the main thread for handling results
                .subscribe(
                        items ->
                                _archivedNotes.setValue(listArchivedNotes),
                        throwable ->
                                Log.e(TAG, "Received items Error: " + throwable.getMessage())
                );
    }

    public void saveNote(Note note) {
        repositoryInterface.insertNote(note);
    }

    public void updateNote(Note note) {
        updateNotesDisposable = repositoryInterface.updateNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updatedRows -> {
                            if (updatedRows > 0) {
                                if (isArchivedNotes) {
                                    getArchivedNotes();
                                } else {
                                    getAllNotes();
                                }
                            } else {
                                Log.w(TAG, "Failed to update note status");
                            }
                        },
                        throwable ->
                                Log.e(TAG, "Error update note status", throwable)
                );
    }

    public void deleteNote(int noteID) {
        deleteNotesDisposable = repositoryInterface.deleteNote(noteID)
                .subscribeOn(Schedulers.io()) // Perform deletion on a background thread
                .observeOn(AndroidSchedulers.mainThread()) // Optionally, handle success/error on a separate thread
                .subscribe(
                        deletedRows -> {
                            if (deletedRows > 0) {
                                if (isArchivedNotes) {
                                    getArchivedNotes();
                                } else {
                                    getAllNotes();
                                }
                            } else {
                                Log.w(TAG, "Failed to delete note status");
                            }
                        },
                        throwable ->
                                Log.e(TAG, "Error deleting note status", throwable)
                );
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
        boolean isList = settings.second;
        if (!sortAction.equals(_sortAction)) {
            _sortAction = sortAction;
            getAllNotes();
        }

        if (!Objects.equals(_isListView.getValue(), isList)) {
            _isListView.setValue(isList);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposeDisposable(notesDisposable);
        disposeDisposable(archivedNotesDisposable);
        disposeDisposable(updateNotesDisposable);
        disposeDisposable(deleteNotesDisposable);
    }

    private void disposeDisposable(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}