package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
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
import com.abdelrahman.rafaat.notesapp.utils.Utils;

import java.util.Locale;


public class ShowNoteFragment extends Fragment {
    private FragmentShowBinding binding;
    private NoteViewModel noteViewModel;
    private Note currentNote;
    private AlertDialog alertDialog;
    private boolean isUnLock = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShowBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkRTL();
        initUi();
        showNoteDetails();
        binding.showNoteBodyTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    private void showNoteDetails() {
        currentNote = Utils.note;
        binding.showNoteTitleTextView.setText(currentNote.getTitle());
        int textSize = currentNote.getTextFormat().getTextSize();
        int textAlignment = currentNote.getTextFormat().getTextAlignment();
        binding.showNoteBodyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        binding.showNoteBodyTextView.setGravity(textAlignment);

        Html.ImageGetter getter = source -> {
            Bitmap bitmap = BitmapFactory.decodeFile(source);
            BitmapDrawable drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            return drawable;
        };

        Spanned noteBody = Html.fromHtml(currentNote.getBody(), Html.FROM_HTML_MODE_LEGACY, getter, null);
        binding.showNoteBodyTextView.setText(noteBody);
        if (currentNote.getColor() != -1)
            binding.showNoteRootView.setBackgroundColor(currentNote.getColor());
        else {
            int color = getResources().getColor(R.color.defaultBackGround, null);
            binding.showNoteRootView.setBackgroundColor(color);
        }
        if (currentNote.getPassword().isEmpty()) {
            binding.unlockNoteImageView.setImageResource(R.drawable.ic_lock);
            isUnLock = false;
        }

    }

    private void initUi() {
        binding.editNoteImageView.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_showNote_to_editNote)
        );

        binding.unlockNoteImageView.setOnClickListener(v -> {
            initViewModel();
            updatePassword();
        });

        binding.goBackImageView.setOnClickListener(v -> Navigation.findNavController(requireView()).popBackStack());
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
            Navigation.findNavController(requireView()).navigate(R.id.password_fragment);
        }

    }

    private void updateNote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);

        builder.setMessage(getString(R.string.remove_password))
                .setPositiveButton(R.string.remove, (dialog, which) -> {
                    currentNote.setPassword("");
                    noteViewModel.updateNote(currentNote);
                    binding.unlockNoteImageView.setImageResource(R.drawable.ic_lock);
                    isUnLock = false;
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> alertDialog.dismiss());

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Utils.note = null;
    }
}