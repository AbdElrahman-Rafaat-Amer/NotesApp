package com.abdelrahman.rafaat.notesapp.ui.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.model.Note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private static final String TAG = "NotesAdapter";
    private List<Note> notes = new ArrayList<>();
    private OnNotesClickListener onClickListener;

    public NotesAdapter(OnNotesClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row_note, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        Note currentNote = notes.get(position);
        holder.bind(currentNote);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setList(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView pinnedImage;
        private ImageView lockedNote;
        private TextView noteTitle;
        private TextView noteBody;
        private TextView noteDate;
        private CardView rootView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pinnedImage = itemView.findViewById(R.id.pinnedNote_imageView);
            lockedNote = itemView.findViewById(R.id.locked_note_ImageView);
            noteTitle = itemView.findViewById(R.id.noteTitle_textView);
            noteBody = itemView.findViewById(R.id.noteBody_textView);
            noteDate = itemView.findViewById(R.id.noteDate_textView);
            rootView = itemView.findViewById(R.id.root_View);
        }

        void bind(Note currentNote) {
            if (currentNote.getPassword().isEmpty()) {
                noteTitle.setText(currentNote.getTitle());
                noteBody.setText(currentNote.getBody());
                noteDate.setText(currentNote.getDate());
                noteDate.setSelected(true);
                lockedNote.setVisibility(View.GONE);
                setViewVisibility(View.VISIBLE);
            } else {
                lockedNote.setVisibility(View.VISIBLE);
                setViewVisibility(View.GONE);
            }
            if (currentNote.isPinned()) {
                pinnedImage.setVisibility(View.VISIBLE);
              //  Collections.swap(notes, getAdapterPosition(), 0);
            } else
                pinnedImage.setVisibility(View.GONE);

            rootView.setCardBackgroundColor(currentNote.getColor());

            rootView.setOnClickListener(view -> onClickListener.onClickListener(currentNote));

        }

        private void setViewVisibility(int visibility) {
            noteTitle.setVisibility(visibility);
            noteBody.setVisibility(visibility);
            noteDate.setVisibility(visibility);
        }
    }


}

