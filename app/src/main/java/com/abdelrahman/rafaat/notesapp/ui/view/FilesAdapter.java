package com.abdelrahman.rafaat.notesapp.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdelrahman.rafaat.notesapp.databinding.CustomRowFileBinding;

import java.util.ArrayList;
import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {
    private List<String> files = new ArrayList<>();
    private final OnFileClickListener onClickListener;
    private Context context;

    public FilesAdapter(OnFileClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public FilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        CustomRowFileBinding binding =
                CustomRowFileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull FilesAdapter.ViewHolder holder, int position) {
        String fileName = files.get(position);
        holder.bind(fileName);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void setList(List<String> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final CustomRowFileBinding binding;

        public ViewHolder(@NonNull CustomRowFileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String fileName) {
            binding.getRoot().setOnClickListener(view -> onClickListener.onFiledClickListener(fileName));
        }
    }

}

