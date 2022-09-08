package com.abdelrahman.rafaat.notesapp.shownotes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentShowBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;

import java.util.Locale;


public class ShowNoteFragment extends Fragment {

    private FragmentShowBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShowBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkRTL();
        binding.showNoteBodyTextView.setMovementMethod(new ScrollingMovementMethod());

        Note note = (Note) getArguments().getSerializable("NOTE");
        binding.showNoteTitleTextView.setText(note.getTitle());
        binding.showNoteBodyTextView.setText(note.getBody());
        binding.showNoteRootView.setBackgroundColor(note.getColor()/*getResources().getColor(note.getColor(), null)*/);

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