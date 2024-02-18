package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.abdelrahman.rafaat.notesapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}