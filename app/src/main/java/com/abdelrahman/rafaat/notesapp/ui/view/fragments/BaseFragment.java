package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class BaseFragment extends Fragment {

    protected int screenWidth = 0;
    protected int screenHeight = 0;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calculateScreenWidth();
    }

    protected void showSnackBar(View view, String message) {
        Snackbar snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setActionTextColor(Color.WHITE);
        snackBar.getView().setBackgroundColor(Color.RED);
        snackBar.show();
    }

    protected void calculateScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) requireContext()).getWindowManager()
                .getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }
}
