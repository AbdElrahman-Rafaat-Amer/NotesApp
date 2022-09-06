package com.abdelrahman.rafaat.notesapp.home.view;

import androidx.cardview.widget.CardView;

import com.abdelrahman.rafaat.notesapp.model.Note;

public interface OnNotesClickListener {
    void onClickListener(Note note);

    void onLongClick(Note note, CardView cardView);
}
