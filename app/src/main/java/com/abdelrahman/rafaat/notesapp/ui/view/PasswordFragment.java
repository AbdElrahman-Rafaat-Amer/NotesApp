package com.abdelrahman.rafaat.notesapp.ui.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.database.LocalSource;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentPasswordBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.Repository;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NotesViewModelFactory;

import java.util.Locale;


public class PasswordFragment extends Fragment {
    private static final String TAG = "PasswordFragment";
    private FragmentPasswordBinding binding;
    private Note note;
    private Boolean isSetPassword = false;
    private NoteViewModel noteViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPasswordBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkIsSetPassword();
        checkRTL();
        binding.goBackImageView.setOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());

        binding.notePinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.passwordErrorTextView.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    if (isSetPassword)
                        updateNote();
                    else
                        checkPassword();
                }

            }
        });

    }

    private void checkIsSetPassword() {
        note = (Note) getArguments().getSerializable("NOTE");
        if (note.getPassword().isEmpty()) {
            isSetPassword = true;
            binding.passwordTitleTextView.setText(R.string.enter_password);
            initViewModel();
        }

    }

    private void checkRTL() {
        String language = Locale.getDefault().getDisplayName();
        int directionality = Character.getDirectionality(language.charAt(0));
        boolean isRTL = directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
        if (isRTL)
            binding.goBackImageView.setImageResource(R.drawable.ic_arrow_right);
    }

    private void checkPassword() {
        if (binding.notePinView.getText().toString().equals(note.getPassword())) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("NOTE", note);
            Navigation.findNavController(getView()).popBackStack();
            Navigation.findNavController(getView()).navigate(R.id.show_note_fragment, bundle);
        } else
            binding.passwordErrorTextView.setVisibility(View.VISIBLE);

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

    private void updateNote() {
        note.setPassword(binding.notePinView.getText().toString());
        noteViewModel.updateNote(note);
        Navigation.findNavController(getView()).popBackStack();
    }
}