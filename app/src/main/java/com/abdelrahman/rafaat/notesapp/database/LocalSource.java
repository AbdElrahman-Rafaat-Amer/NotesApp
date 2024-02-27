package com.abdelrahman.rafaat.notesapp.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.SortAction;
import com.abdelrahman.rafaat.notesapp.model.SortOrder;
import com.abdelrahman.rafaat.notesapp.model.SortType;

import java.util.List;

public class LocalSource implements LocalSourceInterface {
    private static LocalSource localSource = null;
    private NotesDAO dao;
    private LiveData<List<Note>> notes;

    public LocalSource(Context context) {
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        dao = db.notesDAO();
    }

    public static LocalSource getInstance(Context context) {
        if (localSource == null) {
            localSource = new LocalSource(context);
        }
        return localSource;
    }

    @Override
    public void insertNote(Note note) {
        new Thread(() -> dao.insertNote(note)).start();
    }

    @Override
    public LiveData<List<Note>> getAllNotes() {
        return notes;
    }

    @Override
    public LiveData<List<Note>> getAllNotes(SortAction sortAction) {
        LiveData<List<Note>> notes = null;
        SortOrder sortOrder = sortAction.getSortOrder();

        switch (sortAction.getSortType()) {
            case PINNED_NOTES:
                if (sortOrder == SortOrder.ASC) {
                    notes = dao.getAllPinnedNotesAscending();
                } else {
                    notes = dao.getAllPinnedNotesDescending();
                }
                break;
            case CREATION_DATE:
                if (sortOrder == SortOrder.ASC) {
                    notes = dao.getAllNotesAscendingByCreationData();
                } else {
                    notes = dao.getAllNotesDescendingByCreationData();
                }
                break;
            case MODIFICATION_DATE:
                if (sortOrder == SortOrder.ASC) {
                    notes = dao.getAllNotesAscendingByModificationDate();
                } else {
                    notes = dao.getAllDescendingByModificationDate();
                }
                break;
            case TITLE:
                if (sortOrder == SortOrder.ASC) {
                    notes = dao.getAllNotesAscendingByTitle();
                } else {
                    notes = dao.getAllDescendingByTitle();
                }
                break;
            case LOCKED_NOTES:
                if (sortOrder == SortOrder.ASC) {
                    notes = dao.getAllLockedNotesAscending();
                } else {
                    notes = dao.getAllLockedNotesDescending();
                }
                break;
        }

        return notes;
    }

    @Override
    public List<Note> getArchivedNotes() {
        return dao.getArchivedNotes();
    }

    @Override
    public void updateNote(Note note) {
        new Thread(() -> dao.updateNote(note)).start();
    }

    @Override
    public void deleteNote(int id) {
        new Thread(() -> dao.deleteNote(id)).start();
    }
}
