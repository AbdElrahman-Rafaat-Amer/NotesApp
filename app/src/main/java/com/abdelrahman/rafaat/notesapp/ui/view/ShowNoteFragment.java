package com.abdelrahman.rafaat.notesapp.ui.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.database.LocalSource;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentShowBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.Repository;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NotesViewModelFactory;


import java.util.Locale;


public class ShowNoteFragment extends Fragment {
    private FragmentShowBinding binding;
    private NoteViewModel noteViewModel;
    private Note note;
    private AlertDialog alertDialog;
    private boolean isUnLock = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShowBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.showNoteBodyTextView.setMovementMethod(new ScrollingMovementMethod());
        checkRTL();
        initUi();
        showNoteDetails();


        binding.unlockNoteImageView.setOnClickListener(v -> {
            initViewModel();
            updatePassword();
        });
    }

    private void showNoteDetails() {
        note = (Note) getArguments().getSerializable("NOTE");
        binding.showNoteTitleTextView.setText(note.getTitle());
        binding.showNoteBodyTextView.setText(note.getBody());
        binding.showNoteRootView.setBackgroundColor(note.getColor());
        if (note.getPassword().isEmpty()) {
            binding.unlockNoteImageView.setImageResource(R.drawable.ic_lock);
            isUnLock = false;
        }

    }

    private void initUi() {
        binding.editNoteImageView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("NOTE", note);
                    Navigation.findNavController(getView()).navigate(R.id.action_showNote_to_editNote, bundle);
                }
        );

        binding.goBackImageView.setOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
    }

    private void checkRTL() {
        String language = Locale.getDefault().getDisplayName();
        int directionality = Character.getDirectionality(language.charAt(0));
        boolean isRTL = directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
        if (isRTL)
            binding.goBackImageView.setImageResource(R.drawable.ic_arrow_right);
    }

    private void initViewModel() {
        NotesViewModelFactory viewModelFactory = new NotesViewModelFactory(
                Repository.getInstance(
                        LocalSource.getInstance(getContext()), getActivity().getApplication()
                ), getActivity().getApplication()
        );

        noteViewModel = new ViewModelProvider(
                this,
                viewModelFactory
        ).get(NoteViewModel.class);
    }

    private void updatePassword() {
        if (isUnLock)
            updateNote();
        else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("NOTE", note);
            Navigation.findNavController(getView()).navigate(R.id.password_fragment, bundle);
        }

    }

    private void updateNote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);

        builder.setMessage(getString(R.string.remove_password))
                .setPositiveButton(R.string.remove, (dialog, which) -> {
                    note.setPassword("");
                    noteViewModel.updateNote(note);
                    binding.unlockNoteImageView.setImageResource(R.drawable.ic_lock);
                    isUnLock = false;
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> alertDialog.dismiss());

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }
}