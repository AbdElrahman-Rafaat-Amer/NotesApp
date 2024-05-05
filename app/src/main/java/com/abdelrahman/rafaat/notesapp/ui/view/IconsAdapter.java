package com.abdelrahman.rafaat.notesapp.ui.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdelrahman.rafaat.notesapp.databinding.CustomRowIconBinding;
import com.abdelrahman.rafaat.notesapp.interfaces.OnIconClickListener;

import java.util.List;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.ViewHolder> {
    private List<Integer> icons;
    private final OnIconClickListener onClickListener;
    private int selectedItemPosition;

    public IconsAdapter(OnIconClickListener onClickListener, List<Integer> icons) {
        this.icons = icons;
        this.onClickListener = onClickListener;
        selectedItemPosition = icons.size() -1;
    }

    @NonNull
    @Override
    public IconsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomRowIconBinding binding =
                CustomRowIconBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull IconsAdapter.ViewHolder holder, int position) {
        Integer currentNote = icons.get(position);
        holder.bind(currentNote);
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public void setList(List<Integer> icons) {
        this.icons = icons;
        notifyDataSetChanged();
    }

    public void setIconSelected(int icon) {
        selectedItemPosition = icons.indexOf(icon);
        notifyItemChanged(selectedItemPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final CustomRowIconBinding binding;

        public ViewHolder(@NonNull CustomRowIconBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Integer currentIcon) {
            binding.iconImageView.setImageResource(currentIcon);
            binding.iconImageView.setSelected(selectedItemPosition == getAdapterPosition());
            binding.getRoot().setOnClickListener(view -> {
                onClickListener.onIconClickListener(getAdapterPosition());
                notifyItemChanged(selectedItemPosition); // Update previous selected item
                selectedItemPosition = getAdapterPosition();
                notifyItemChanged(selectedItemPosition);// Update currently selected item
            });
        }
    }

}
