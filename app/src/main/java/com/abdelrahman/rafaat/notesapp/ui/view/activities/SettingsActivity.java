package com.abdelrahman.rafaat.notesapp.ui.view.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private NoteViewModel noteViewModel;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);

            manageThemeMode();
            managePasswordsScreenTime();
        }

        private void manageThemeMode() {
            ListPreference listPreference = findPreference("THEME_MODE");
            if (listPreference != null) {
                listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    String selectedValue = (String) newValue;
                    int selectMode = Integer.parseInt(selectedValue);
                    switch (selectMode) {
                        case AppCompatDelegate.MODE_NIGHT_YES:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            break;

                        case AppCompatDelegate.MODE_NIGHT_NO:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            break;

                        case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                        default:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            break;
                    }
                    return true;
                });
            }
        }

        private void managePasswordsScreenTime() {
            ListPreference listPreference = findPreference("PASSWORDS_TIME");
            if (listPreference != null) {
                listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    String selectedValue = (String) newValue;
                    int selectedTime = Integer.parseInt(selectedValue);
                    if (selectedTime == 1) {
                        showCustomDialog();
                    }
                    return true;
                });
            }
        }

        public void showCustomDialog() {
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View dialogView = inflater.inflate(R.layout.dialog_time_layout, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(dialogView);

            EditText editText = dialogView.findViewById(R.id.editText);
            Button button = dialogView.findViewById(R.id.button);

            AlertDialog dialog = builder.create();

            button.setOnClickListener(v -> {
                String inputText = editText.getText().toString();

                if (!inputText.isEmpty()) {
                    int timeInMinutes = Integer.parseInt(inputText);
                    noteViewModel.saveTimeToOpenPasswordsScreen(timeInMinutes);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

    }
}