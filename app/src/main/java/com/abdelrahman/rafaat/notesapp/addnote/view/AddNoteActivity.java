package com.abdelrahman.rafaat.notesapp.addnote.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.addnote.viewmodel.AddNoteViewModel;
import com.abdelrahman.rafaat.notesapp.addnote.viewmodel.AddNotesViewModelFactory;
import com.abdelrahman.rafaat.notesapp.database.LocalSource;
import com.abdelrahman.rafaat.notesapp.databinding.ActivityAddNoteBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.Repository;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {

    private String TAG = "AddNoteActivity";
    private ActivityAddNoteBinding binding;
    private AddNoteViewModel noteViewModel;
    private AddNotesViewModelFactory viewModelFactory;
    private boolean isTextChanged = false;
    private AlertDialog alertDialog;
    private boolean isUpdate = false;
    private Note note;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            note = (Note) getIntent().getSerializableExtra("NOTE");
            binding.noteTitleEditText.setText(note.getTitle());
            binding.noteBodyEditText.setText(note.getBody());
            isUpdate = true;
            Log.i(TAG, "onCreate: id------------> " + note.getId());
        } catch (Exception exception) {
            Log.i(TAG, "onCreate: exception -----------------> " + exception.getMessage());
        }


        initViewModel();

        binding.goBackImageView.setOnClickListener(v -> {
            if (isTextChanged)
                showDialog();
        });

        binding.saveImageView.setOnClickListener(v -> {
            if (checkTitle() & checkBody()) {
                if (isUpdate)
                    updateNote();
                else
                    saveNote();
            }
        });

        binding.noteBodyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isTextChanged = true;
            }
        });

        binding.noteTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isTextChanged = true;
            }
        });

    }

    private void initViewModel() {
        viewModelFactory = new AddNotesViewModelFactory(
                Repository.getInstance(
                        LocalSource.getInstance(this), this.getApplication()
                ), this.getApplication()
        );

        noteViewModel = new ViewModelProvider(
                this,
                viewModelFactory
        ).get(AddNoteViewModel.class);
    }

    @Override
    public void onBackPressed() {
        if (isTextChanged)
            showDialog();
        else
            super.onBackPressed();
    }

    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.cancel_changes)
                .setMessage(R.string.ask_cancel_changes)
                .setPositiveButton(R.string.yes, (dialog, which) -> finish())
                .setNegativeButton(R.string.no, (dialog, which) -> alertDialog.dismiss());

        alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean checkTitle() {
        boolean isTitleTrue = false;
        if (binding.noteTitleEditText.getText().toString().trim().isEmpty())
            binding.noteTitleEditText.setError(getString(R.string.title_error));
        else {
            binding.noteTitleEditText.setError(null);
            isTitleTrue = true;
        }
        return isTitleTrue;
    }

    private boolean checkBody() {
        boolean isBodyTrue = false;
        if (binding.noteBodyEditText.getText().toString().trim().isEmpty())
            binding.noteBodyEditText.setError(getString(R.string.body_error));
        else {
            binding.noteBodyEditText.setError(null);
            isBodyTrue = true;
        }
        return isBodyTrue;
    }

    private void saveNote() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MM yyy HH:mm a");
        Note note = new Note(binding.noteTitleEditText.getText().toString(),
                binding.noteBodyEditText.getText().toString(),
                formatter.format(new Date()));

        noteViewModel.saveNote(note);
        finish();
    }


    private void updateNote() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MM yyy HH:mm a");
        note.setTitle(binding.noteTitleEditText.getText().toString());
        note.setBody(binding.noteBodyEditText.getText().toString());
        note.setDate(formatter.format(new Date()));
        /*note = new Note(binding.noteTitleEditText.getText().toString(),
                binding.noteBodyEditText.getText().toString(),
                formatter.format(new Date()));*/
        Log.i(TAG, "updateNote: id------------->" + note.getId());
        noteViewModel.updateNote(note);
        finish();
    }
}