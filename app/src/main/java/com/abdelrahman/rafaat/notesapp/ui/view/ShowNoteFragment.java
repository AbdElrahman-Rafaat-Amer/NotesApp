package com.abdelrahman.rafaat.notesapp.ui.view;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentShowBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.chaos.view.PinView;

import java.util.Locale;


public class ShowNoteFragment extends Fragment {
    private static final String TAG = "ShowNoteFragment";
    private FragmentShowBinding binding;
    private Note note;

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


    }

    private void showNoteDetails() {
        note = (Note) getArguments().getSerializable("NOTE");
        binding.showNoteTitleTextView.setText(note.getTitle());
        binding.showNoteBodyTextView.setText(note.getBody());
        binding.showNoteRootView.setBackgroundColor(note.getColor());
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

}