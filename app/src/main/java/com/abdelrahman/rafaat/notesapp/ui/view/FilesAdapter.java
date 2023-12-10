package com.abdelrahman.rafaat.notesapp.ui.view;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.CustomRowFileBinding;
import com.abdelrahman.rafaat.notesapp.databinding.CustomRowFolderBinding;
import com.abdelrahman.rafaat.notesapp.model.Folder;

import java.util.ArrayList;
import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Folder> folders = new ArrayList<>();
    private final OnFolderClickListener onClickListener;
    private final ViewTypes viewType;
    private Context context;

    public FilesAdapter(OnFolderClickListener onClickListener, ViewTypes viewType) {
        this.onClickListener = onClickListener;
        this.viewType = viewType;
    }

    @Override
    public int getItemViewType(int position) {
        return viewType.getValue();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == ViewTypes.VIEW_TYPE_ITEM_1.getValue()) {
            CustomRowFileBinding binding =
                    CustomRowFileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new FileViewHolder(binding);
        } else {
            CustomRowFolderBinding binding =
                    CustomRowFolderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new FolderViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 1:
                FileViewHolder fileViewHolder = (FileViewHolder) holder;
                Folder folder = folders.get(position);
                fileViewHolder.bind(folder, position);
                break;

            case 2:
                FolderViewHolder folderViewHolder = (FolderViewHolder) holder;
                Folder folder2 = folders.get(position);
                folderViewHolder.bind(folder2);
                break;
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        switch (viewType) {
            case VIEW_TYPE_ITEM_1:
                size = folders.size();
                break;
            case VIEW_TYPE_ITEM_2:
                size = folders.size() - 1;
                break;
        }
        return size;
    }

    public void setList(List<Folder> folders) {
        this.folders = folders;
        notifyDataSetChanged();
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {
        public final CustomRowFileBinding binding;

        public FileViewHolder(@NonNull CustomRowFileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Folder folder, int position) {
            boolean isLastItem = position == folders.size() - 1;
            if (isLastItem) {
                binding.showAllFoldersImage.setVisibility(View.VISIBLE);
                binding.noteTitleTextView.setVisibility(View.INVISIBLE);
            } else {
                binding.showAllFoldersImage.setVisibility(View.INVISIBLE);
                binding.noteTitleTextView.setVisibility(View.VISIBLE);
                binding.noteTitleTextView.setText(folder.getName());
            }

            if (folder.isChecked()){
                binding.folderCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.selected_card_view_color));
            }else{
                binding.folderCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_view_color));
            }
            binding.getRoot().setOnClickListener(view -> onClickListener.onFolderClickListener(folder));
        }
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {
        public final CustomRowFolderBinding binding;

        public FolderViewHolder(@NonNull CustomRowFolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Folder folder) {
            Log.i("AllFoldersFragment", "bind: folder--------> " + folder.toString());
            binding.getRoot().setOnClickListener(view -> onClickListener.onFolderClickListener(folder));
            binding.folderNotesNumberTextView.setText("" + folder.getNumberOfNotes());
            binding.folderTitleTextView.setText(folder.getName());
            if (folder.isChecked()) {
                binding.checkedImageView.setVisibility(View.VISIBLE);
            } else {
                binding.checkedImageView.setVisibility(View.INVISIBLE);
            }
            //Handel case when it is pin
        }
    }

}

