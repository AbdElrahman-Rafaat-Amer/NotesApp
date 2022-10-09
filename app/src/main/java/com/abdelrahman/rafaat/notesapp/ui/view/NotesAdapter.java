package com.abdelrahman.rafaat.notesapp.ui.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.Utils;
import com.abdelrahman.rafaat.notesapp.databinding.CustomRowNoteBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private List<Note> notes = new ArrayList<>();
    private OnNotesClickListener onClickListener;
    private Context context;

    public NotesAdapter(OnNotesClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        CustomRowNoteBinding binding =
                CustomRowNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
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
        private CustomRowNoteBinding binding;

        public ViewHolder(@NonNull CustomRowNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Note currentNote) {
            if (currentNote.getPassword().isEmpty()) {
                binding.noteTitleTextView.setText(currentNote.getTitle());
                binding.noteBodyTextView.setText(currentNote.getBody());
                binding.noteDateTextView.setText(currentNote.getDate());
                binding.noteDateTextView.setSelected(true);
                binding.lockedNoteImageView.setVisibility(View.GONE);
                setViewVisibility(View.VISIBLE);
            } else {
                binding.lockedNoteImageView.setVisibility(View.VISIBLE);
                setViewVisibility(View.GONE);
            }
            if (currentNote.isPinned()) {
                binding.pinnedNoteImageView.setVisibility(View.VISIBLE);
            } else
                binding.pinnedNoteImageView.setVisibility(View.GONE);

            if (currentNote.getImagePaths().isEmpty()) {
                binding.noteImageView.setVisibility(View.GONE);
            } else {
                binding.noteImageView.setImageBitmap(BitmapFactory.decodeFile(currentNote.getImagePaths().get(0)));
            }

            if (currentNote.getColor() != -1)
                binding.getRoot().setCardBackgroundColor(currentNote.getColor());
            else {
                int color = context.getResources().getColor(R.color.defaultBackGround, null);
                binding.getRoot().setCardBackgroundColor(color);
            }

            binding.getRoot().setOnClickListener(view -> onClickListener.onClickListener(currentNote));

        }

        private void setViewVisibility(int visibility) {
            binding.noteTitleTextView.setVisibility(visibility);
            binding.noteBodyTextView.setVisibility(visibility);
            binding.noteDateTextView.setVisibility(visibility);
        }
    }


}

