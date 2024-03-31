package com.abdelrahman.rafaat.notesapp.database;

import android.content.Context;

import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.SortAction;
import com.abdelrahman.rafaat.notesapp.model.SortOrder;
import com.abdelrahman.rafaat.notesapp.model.SortType;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class LocalSource implements LocalSourceInterface {
    private static LocalSource localSource = null;
    private final NotesDAO dao;

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
    public List<Note> getAllNotes(SortAction sortAction) {

        List<Note> notes = new ArrayList<>();
        SortType sortType = sortAction.getSortType();
        SortOrder sortOrder = sortAction.getSortOrder();

        switch (sortType) {
            case PINNED_NOTES:
                notes = (sortOrder == SortOrder.ASCENDING) ? dao.getAllNotesPinnedAscending() : dao.getAllNotesPinnedDescending();
                break;
            case CREATION_DATE:
                notes = (sortOrder == SortOrder.ASCENDING) ? dao.getAllNotesAscendingByCreationDate() : dao.getAllNotesDescendingByCreationDate();
                break;
            case MODIFICATION_DATE:
                notes = (sortOrder == SortOrder.ASCENDING) ? dao.getAllNotesAscendingByModificationDate() : dao.getAllNotesDescendingByModificationDate();
                break;
            case TITLE:
                notes = (sortOrder == SortOrder.ASCENDING) ? dao.getAllNotesAscendingByTitle() : dao.getAllNotesDescendingByTitle();
                break;
            case LOCKED_NOTES:
                notes = (sortOrder == SortOrder.ASCENDING) ? dao.getAllNotesLockedNotesAscending() : dao.getAllNotesLockedNotesDescending();
                break;
        }

        return notes;
    }

    @Override
    public List<Note> getArchivedNotes() {
        return dao.getArchivedNotes();
    }

    @Override
    public Single<Integer> updateNote(Note note) {
        return dao.updateNote(note);
    }

    @Override
    public Single<Integer> deleteNote(int id) {
        return dao.deleteNote(id);
    }
}
