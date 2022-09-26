package com.abdelrahman.rafaat.notesapp.ui.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.abdelrahman.rafaat.notesapp.EditTextViewExtended;
import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.database.LocalSource;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentAddNoteBinding;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NotesViewModelFactory;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.model.Repository;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

public class AddNoteFragment extends Fragment {

    public static final int PICK_IMAGE = 1;
    private static final String TAG = "AddNoteFragment";
    private FragmentAddNoteBinding binding;
    private NoteViewModel noteViewModel;
    private boolean isTextChanged = false;
    private Note note;
    private int noteColor;
    private boolean isUpdate = false;
    private boolean isColorsVisiable = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddNoteBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        noteColor = getResources().getColor(R.color.white, null);
        checkIsEdit();
        checkRTL();
        initViewModel();
        watchText();
        onBackPressed();

        binding.goBackImageView.setOnClickListener(v -> {
            if (isTextChanged) {
                if (isUpdate)
                    addDialogNote(getString(R.string.save_changes));
                else
                    addDialogNote(getString(R.string.discard_note));
            } else
                Navigation.findNavController(view).popBackStack();

        });


        binding.saveImageView.setOnClickListener(v -> {
            if (checkTitle() & checkBody()) {
                if (isUpdate)
                    updateNote(v);
                else
                    saveNote(v);
            }
        });

        binding.textOptionsTextView.setOnClickListener(v -> {
            showTextOptions();
        });

        binding.choseImageImageView.setOnClickListener(v -> {
            openGallery();
        });

        binding.choseColorImageView.setOnClickListener(v -> {
            if (binding.colorsBar.getVisibility() == View.GONE) {
                binding.colorsBar.setVisibility(View.VISIBLE);
                // binding.colorPickerView.setVisibility(View.VISIBLE);
                //   binding.closeColorPickerView.setVisibility(View.VISIBLE);
                isColorsVisiable = true;
                binding.helperBar.setVisibility(View.INVISIBLE);
                chooseColor();
            } else {
                //      binding.colorPickerView.setVisibility(View.GONE);
                //  binding.closeColorPickerView.setVisibility(View.GONE);
                binding.colorsBar.setVisibility(View.GONE);
            }

        });

        binding.closeColorPickerView.setOnClickListener(v -> {
            binding.colorsBar.setVisibility(View.GONE);
            isColorsVisiable = false;
        });

        binding.closeTextPickerView.setOnClickListener(v -> {
            binding.textBar.setVisibility(View.GONE);
            isColorsVisiable = false;
        });

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {

            Rect r = new Rect();
            binding.getRoot().getWindowVisibleDisplayFrame(r);
            int screenHeight = binding.getRoot().getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                if (!isColorsVisiable)
                    binding.helperBar.setVisibility(View.VISIBLE);
            } else
                binding.helperBar.setVisibility(View.GONE);

        });

    }

    private void showTextOptions() {
        binding.textBar.setVisibility(View.VISIBLE);
        binding.bold.setOnClickListener(v -> {
            binding.noteBodyEditText.setTypeface(null, Typeface.BOLD);
        });

        binding.normal.setOnClickListener(v -> {
            binding.noteBodyEditText.setTypeface(null, Typeface.NORMAL);
        });

        binding.italic.setOnClickListener(v -> {
            binding.noteBodyEditText.setTypeface(null, Typeface.ITALIC);
        });

        binding.viewStart.setOnClickListener(v -> {
            binding.noteBodyEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        });

        binding.center.setOnClickListener(v -> {
            binding.noteBodyEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        });

        binding.viewEnd.setOnClickListener(v -> {
            binding.noteBodyEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        });

        binding.ltr.setOnClickListener(v -> {
            binding.noteBodyEditText.setTextDirection(View.TEXT_DIRECTION_LTR);
        });

        binding.rtl.setOnClickListener(v -> {
            binding.noteBodyEditText.setTextDirection(View.TEXT_DIRECTION_RTL);
        });
    }

    private void checkIsEdit() {
        try {
            note = (Note) getArguments().getSerializable("NOTE");
            binding.noteTitleEditText.setText(note.getTitle());
            binding.noteBodyEditText.setText(note.getBody());
            noteColor = note.getColor();
            isUpdate = true;
            int[] colors = getResources().getIntArray(R.array.colors);
            int colorIndex = Arrays.stream(colors).boxed()
                    .collect(Collectors.toList())
                    .indexOf(noteColor);
            RadioButton radioButton = (RadioButton) binding.colorPickerView.findViewWithTag(colorIndex + "");
            radioButton.setChecked(true);
        } catch (Exception exception) {
            Log.i(TAG, "onViewCreated: exception-----------------> " + exception.getMessage());
        }
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


    private void chooseColor() {
        binding.colorPickerView.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedOption = binding.colorPickerView.getCheckedRadioButtonId();
            RadioButton radioButton = getView().findViewById(selectedOption);
            int color = Integer.parseInt(radioButton.getTag().toString());
            int[] colors = getResources().getIntArray(R.array.colors);
            noteColor = colors[color];
            binding.getRoot().setBackgroundColor(noteColor);
        });
    }

    private void watchText() {
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

    public void onBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.i(TAG, "handleOnBackPressed: ------------------------");
                if (isTextChanged) {
                    if (isUpdate)
                        addDialogNote(getString(R.string.save_changes));
                    else
                        addDialogNote(getString(R.string.discard_note));
                } else
                    Navigation.findNavController(getView()).popBackStack();
            }
        });
    }

    private void addDialogNote(String message) {
        View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView dialogMessage = view.findViewById(R.id.dialog_message_textView);
        Button saveButton = view.findViewById(R.id.save_button);
        Button discardButton = view.findViewById(R.id.discard_button);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        alertDialog.getWindow().setLayout(
                (int) (displayRectangle.width() * 0.82f),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogMessage.setText(message);

        saveButton.setOnClickListener(v -> {
            if (isUpdate)
                updateNote(getView());
            else
                saveNote(getView());
            alertDialog.dismiss();
        });

        discardButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            Navigation.findNavController(getView()).popBackStack();
        });

    }

    private boolean checkTitle() {
        boolean isTitleTrue = false;
        if (binding.noteTitleEditText.getText().toString().trim().isEmpty())
            Toast.makeText(getContext(), getText(R.string.title_error), Toast.LENGTH_SHORT).show();
        else
            isTitleTrue = true;

        return isTitleTrue;
    }

    private boolean checkBody() {
        boolean isBodyTrue = false;
        if (binding.noteBodyEditText.getText().toString().trim().isEmpty())
            Toast.makeText(getContext(), getText(R.string.body_error), Toast.LENGTH_SHORT).show();
        else
            isBodyTrue = true;

        return isBodyTrue;
    }

    private void saveNote(View view) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MM yyy HH:mm a");
        Note note = new Note(binding.noteTitleEditText.getText().toString(),
                binding.noteBodyEditText.getText().toString(),
                formatter.format(new Date()),
                noteColor);

        noteViewModel.saveNote(note);
        Navigation.findNavController(view).popBackStack();
    }

    private void updateNote(View view) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MM yyy HH:mm a");
        note.setTitle(binding.noteTitleEditText.getText().toString());
        note.setBody(binding.noteBodyEditText.getText().toString());
        note.setDate(formatter.format(new Date()));
        note.setColor(noteColor);
        noteViewModel.updateNote(note);
        Navigation.findNavController(view).popBackStack();
    }

    private void openGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("OnActivityResult");

        if (requestCode == PICK_IMAGE) {
            if (data == null) {
                Log.i(TAG, "onActivityResult: error in getting image from gallery");
            } else {
                setImageToImageView(data);
            }
        }
    }

    private void setImageToImageView(Intent data) {
        Log.i(TAG, "onActivityResult:load image success");
        Log.i(TAG, "onActivityResult: " + data.getData());

        try {
            Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), data.getData());
            Log.i(TAG, "onActivityResult: bitmapImage " + bitmapImage);
            EditTextViewExtended.insertImageToCurrentSelection(bitmapImage, binding.noteBodyEditText);
        } catch (IOException e) {
            Log.i(TAG, "onActivityResult: IOException " + e.getMessage());
        }
    }


}