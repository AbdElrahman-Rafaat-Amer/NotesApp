package com.abdelrahman.rafaat.notesapp.addnote.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private int color;

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
        choseColor();
        binding.goBackImageView.setOnClickListener(v -> {
            if (isTextChanged) {
                if (isUpdate)
                    addDialogNote(getString(R.string.save_changes));
                else
                    addDialogNote(getString(R.string.discard_note));
            } else
                finish();
        });

        binding.saveImageView.setOnClickListener(v -> {
            if (checkTitle() & checkBody()) {
                if (isUpdate)
                    updateNote();
                else
                    saveNote();
            }
        });

        binding.colorImageView.setOnClickListener(v -> {
            if (binding.colorRootView.getVisibility() == View.GONE)
                binding.colorRootView.setVisibility(View.VISIBLE);
            else
                binding.colorRootView.setVisibility(View.GONE);
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

    private void choseColor() {
        binding.color1.setOnClickListener(v -> color = R.color.color1);

        binding.color2.setOnClickListener(v -> color = R.color.color2);

        binding.color3.setOnClickListener(v -> color = R.color.color3);

        binding.color4.setOnClickListener(v -> color = R.color.color4);

        binding.color5.setOnClickListener(v -> color = R.color.color5);

        binding.color6.setOnClickListener(v -> color = R.color.color6);

        binding.color7.setOnClickListener(v -> color = R.color.color7);

        binding.color8.setOnClickListener(v -> color = R.color.color8);
    }

    @Override
    public void onBackPressed() {
        if (isTextChanged) {
            if (isUpdate)
                addDialogNote(getString(R.string.save_changes));
            else
                addDialogNote(getString(R.string.discard_note));
        } else
            super.onBackPressed();
    }

    private void addDialogNote(String message) {
        View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView dialogMessage = view.findViewById(R.id.dialog_message_textView);
        Button saveButton = view.findViewById(R.id.save_button);
        Button discardButton = view.findViewById(R.id.discard_button);


        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        alertDialog.getWindow().setLayout(
                (int) (displayRectangle.width() * 0.82f),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogMessage.setText(message);

        saveButton.setOnClickListener(v -> {
            if (isUpdate)
                updateNote();
            else
                saveNote();
        });

        discardButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            finish();
        });

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
                formatter.format(new Date()),
                color);

        noteViewModel.saveNote(note);
        finish();
    }

    private void updateNote() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MM yyy HH:mm a");
        note.setTitle(binding.noteTitleEditText.getText().toString());
        note.setBody(binding.noteBodyEditText.getText().toString());
        note.setDate(formatter.format(new Date()));
        note.setColor(color);
        noteViewModel.updateNote(note);
        finish();
    }
}