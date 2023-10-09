package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.graphics.Color;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public class BaseFragment extends Fragment {

    protected void showSnackBar(View view, String message) {
        Snackbar snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setActionTextColor(Color.WHITE);
        snackBar.getView().setBackgroundColor(Color.RED);
        snackBar.show();
    }
}
