package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import static androidx.navigation.Navigation.findNavController;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.ui.view.NotesAdapter;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;
import com.google.android.material.snackbar.Snackbar;

public class BaseFragment extends Fragment {

    protected AlertDialog alertDialog;
    protected NoteViewModel noteViewModel;
    protected Note selectedNote;
    protected NotesAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
    }

    private void initViewModel() {
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
    }

    protected void onBackPressed() {

    }

    protected void showSnackBar(View view, String message) {
        Snackbar snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setActionTextColor(Color.WHITE);
        snackBar.getView().setBackgroundColor(Color.RED);
        snackBar.show();
    }

    protected void showAlertDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);

        builder.setMessage(getString(R.string.remove_note))
                .setPositiveButton(R.string.remove, (dialog, which) -> noteViewModel.deleteNote(selectedNote.getId()))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    alertDialog.dismiss();
                    adapter.notifyItemChanged(position);
                });

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    protected void navigate(@IdRes int resId, Bundle args) {
        findNavController(requireView()).navigate(resId, args);
    }
}
