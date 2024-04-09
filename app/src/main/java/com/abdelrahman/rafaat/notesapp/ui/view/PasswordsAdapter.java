package com.abdelrahman.rafaat.notesapp.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdelrahman.rafaat.notesapp.databinding.CustomRowPasswordBinding;
import com.abdelrahman.rafaat.notesapp.interfaces.OnIconClickListener;
import com.abdelrahman.rafaat.notesapp.model.Passwords;

import java.util.ArrayList;
import java.util.List;

public class PasswordsAdapter extends RecyclerView.Adapter<PasswordsAdapter.ViewHolder> {

    private final OnIconClickListener onClickListener;
    private List<Passwords> passwords = new ArrayList<>();

    public PasswordsAdapter(OnIconClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public PasswordsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomRowPasswordBinding binding =
                CustomRowPasswordBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull PasswordsAdapter.ViewHolder holder, int position) {
        Passwords currentNote = passwords.get(position);
        holder.bind(currentNote);
    }

    @Override
    public int getItemCount() {
        return passwords.size();
    }

    public void setList(List<Passwords> passwords) {
        this.passwords = passwords;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final CustomRowPasswordBinding binding;

        public ViewHolder(@NonNull CustomRowPasswordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Passwords currentPassword) {
            binding.websiteImageView.setImageResource(currentPassword.getIcon());
            binding.userNameTextView.setText(currentPassword.getUserName());
            binding.passwordTextView.setText(currentPassword.getPassword());
            if (currentPassword.getWebsiteName().isEmpty()) {
                binding.websiteTextView.setVisibility(View.GONE);
            } else {
                binding.websiteTextView.setVisibility(View.VISIBLE);
                binding.websiteTextView.setText(currentPassword.getWebsiteName());
            }
            binding.getRoot().setOnClickListener(view ->
                    onClickListener.onIconClickListener(getAdapterPosition())
            );

            binding.deleteImageView.setOnClickListener(view ->
                    onClickListener.onDeleteClickListener(getAdapterPosition())
            );
        }

    }

}

