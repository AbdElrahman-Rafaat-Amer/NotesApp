package com.abdelrahman.rafaat.notesapp.home.view;

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
import java.util.List;
import java.util.Random;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private String TAG = "NotesAdapter";
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
        holder.noteTitle.setText(currentNote.getTitle());
        holder.noteBody.setText(currentNote.getBody());
        holder.noteDate.setText(currentNote.getDate());
        holder.noteDate.setSelected(true);
        if (currentNote.isPinned())
            holder.pinnedImage.setVisibility(View.VISIBLE);
        else
            holder.pinnedImage.setVisibility(View.GONE);


        holder.rootView.setCardBackgroundColor(holder.itemView.getResources().getColor(currentNote.getColor(), null));

        holder.rootView.setOnClickListener(view -> onClickListener.onClickListener(currentNote));

        holder.rootView.setOnLongClickListener(view -> {
            onClickListener.onLongClick(currentNote, holder.rootView);
            return true;
        });
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
        private TextView noteTitle;
        private TextView noteBody;
        private TextView noteDate;
        private CardView rootView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pinnedImage = itemView.findViewById(R.id.pinnedNote_imageView);
            noteTitle = itemView.findViewById(R.id.noteTitle_textView);
            noteBody = itemView.findViewById(R.id.noteBody_textView);
            noteDate = itemView.findViewById(R.id.noteDate_textView);
            rootView = itemView.findViewById(R.id.root_View);
        }
    }
}

