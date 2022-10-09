package com.abdelrahman.rafaat.notesapp.ui.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.Utils;
import com.abdelrahman.rafaat.notesapp.database.LocalSource;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentAddNoteBinding;
import com.abdelrahman.rafaat.notesapp.model.Repository;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NotesViewModelFactory;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.FileOutputStream;


public class AddNoteFragment extends Fragment {

    public static final int PICK_IMAGE = 1;
    private static final String TAG = "AddNoteFragment";
    private FragmentAddNoteBinding binding;
    private NoteViewModel noteViewModel;
    private boolean isTextChanged = false;
    private Note note;
    private int noteColor = -1;
    private boolean isUpdate = false;
    private boolean isKeyboardVisible = false;
    private boolean isBodyHasFocus = false;
    private int imageNumber = 0;
    private ArrayList<String> imageIndices = new ArrayList<>();
    private ArrayList<String> imagePaths = new ArrayList<>();
    private static final String FILENAME = "images";
    private Bitmap bitmap;

    private int startCursor = 0;
    private int endCursor = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddNoteBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
        checkIsEdit();
        checkRTL();
        initViewModel();
        watchText();
        watchKeyboard();
        onBackPressed();

    }

    private void initUI() {
        binding.saveImageView.setOnClickListener(v -> {
            if (checkTitle() & checkBody()) {
                if (isUpdate)
                    updateNote(v);
                else
                    saveNote(v);
            }
        });

        binding.textOptionsTextView.setOnClickListener(v -> showTextOptions());

        binding.choseImageImageView.setOnClickListener(v -> openGallery());

        binding.choseColorImageView.setOnClickListener(v -> {
            if (binding.colorsBar.getVisibility() == View.GONE) {
                binding.colorsBar.setVisibility(View.VISIBLE);
                binding.helperBar.setVisibility(View.INVISIBLE);
                chooseColor();
            } else {
                binding.colorsBar.setVisibility(View.GONE);
            }

        });

        binding.closeColorPickerView.setOnClickListener(v -> binding.colorsBar.setVisibility(View.GONE));

        binding.closeTextPickerView.setOnClickListener(v -> binding.textBar.setVisibility(View.GONE));

        binding.goBackImageView.setOnClickListener(v -> {
            if (isTextChanged) {
                if (isUpdate)
                    addDialogNote(getString(R.string.save_changes));
                else
                    addDialogNote(getString(R.string.discard_note));
            } else
                Navigation.findNavController(getView()).popBackStack();

        });

        binding.noteBodyEditText.setOnFocusChangeListener((v, hasFocus) -> {
            isBodyHasFocus = hasFocus;
            if (!hasFocus) {
                hideHelperBar();
            } else if (isKeyboardVisible) {
                binding.helperBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void watchKeyboard() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {

            Rect r = new Rect();
            binding.getRoot().getWindowVisibleDisplayFrame(r);
            int screenHeight = binding.getRoot().getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                isKeyboardVisible = true;
                if (isBodyHasFocus) {
                    binding.helperBar.setVisibility(View.VISIBLE);
                }
            } else {
                isKeyboardVisible = false;
                hideHelperBar();
            }

        });
    }

    private void hideHelperBar() {
        binding.helperBar.setVisibility(View.GONE);
        binding.colorsBar.setVisibility(View.GONE);
        binding.textBar.setVisibility(View.GONE);
    }

    private void showTextOptions() {
        binding.textBar.setVisibility(View.VISIBLE);
        binding.bold.setOnClickListener(v ->
                binding.noteBodyEditText.setTypeface(null, Typeface.BOLD)
        );

        binding.normal.setOnClickListener(v ->
                binding.noteBodyEditText.setTypeface(null, Typeface.NORMAL)
        );

        binding.italic.setOnClickListener(v ->
                binding.noteBodyEditText.setTypeface(null, Typeface.ITALIC)
        );

        binding.viewStart.setOnClickListener(v ->
                binding.noteBodyEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START)
        );

        binding.center.setOnClickListener(v ->
                binding.noteBodyEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER)
        );

        binding.viewEnd.setOnClickListener(v ->
                binding.noteBodyEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END)
        );

        binding.ltr.setOnClickListener(v ->
                binding.noteBodyEditText.setTextDirection(View.TEXT_DIRECTION_LTR)
        );

        binding.rtl.setOnClickListener(v -> binding.noteBodyEditText.setTextDirection(View.TEXT_DIRECTION_RTL));
    }

    private void checkIsEdit() {
        try {
            note = (Note) getArguments().getSerializable("NOTE");
            binding.noteTitleEditText.setText(note.getTitle());
            binding.noteBodyEditText.setText(note.getBody());
            noteColor = note.getColor();
            imageIndices = note.getImageIndices();
            imageNumber = imageIndices.size();
            Log.i(TAG, "checkIsEdit:imageIndices.size() --------------------------------> " + imageIndices.size());
            isUpdate = true;

            if (note.getImageIndices().isEmpty()) {
                binding.noteBodyEditText.setText(note.getBody());
            } else {
                for (int i = 0; i < note.getImageIndices().size(); i++) {
                    imagePaths.add(i, note.getImagePaths().get(i));
                    boolean isSuccess = Utils.insertImageToTextView(BitmapFactory.decodeFile(imagePaths.get(i)), binding.noteBodyEditText, Integer.parseInt(imageIndices.get(i)));
                    if (!isSuccess) {
                        imageNumber--;
                    }
                }
            }

            if (noteColor != -1) {
                binding.getRoot().setBackgroundColor(noteColor);
                int[] colors = getResources().getIntArray(R.array.colors);
                int colorIndex = Arrays.stream(colors).boxed()
                        .collect(Collectors.toList())
                        .indexOf(noteColor);
                RadioButton radioButton = binding.colorPickerView.findViewWithTag(colorIndex + "");
                radioButton.setChecked(true);
            }


        } catch (Exception exception) {
            Log.i(TAG, "checkIsEdit: exception-----------------> " + exception.getMessage());
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
                        LocalSource.getInstance(getContext()), getActivity().getApplicationContext()
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


        binding.noteBodyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                startCursor = binding.noteBodyEditText.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                endCursor = binding.noteBodyEditText.getSelectionStart();
                isTextChanged = true;
                Log.i(TAG, "afterTextChanged: ----------------------");
                getNewIndices();
                Log.i(TAG, "afterTextChanged: ***********************");
            }
        });
    }

    private void onBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
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
        alertDialog.show();

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        alertDialog.getWindow().setLayout(
                (int) (displayRectangle.width() * 0.82f),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogMessage.setText(message);

        saveButton.setOnClickListener(v -> {
            if (checkTitle() & checkBody()) {
                if (isUpdate)
                    updateNote(v);
                else
                    saveNote(v);
            } else {
                alertDialog.dismiss();
            }
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
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MM yyy HH:mm a", Locale.getDefault());
        getNewIndices();
        Note note = new Note(binding.noteTitleEditText.getText().toString(),
                binding.noteBodyEditText.getText().toString(),
                formatter.format(new Date()),
                noteColor, imagePaths, imageIndices);

        noteViewModel.saveNote(note);
        Navigation.findNavController(view).popBackStack();
    }

    private void updateNote(View view) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MM yyy HH:mm a", Locale.getDefault());
        note.setTitle(binding.noteTitleEditText.getText().toString());
        note.setBody(binding.noteBodyEditText.getText().toString());
        note.setDate(formatter.format(new Date()));
        note.setColor(noteColor);
        note.setImagePaths(imagePaths);
        getNewIndices();
        note.setImageIndices(imageIndices);
        noteViewModel.updateNote(note);
        Navigation.findNavController(view).popBackStack();
    }

    private void getNewIndices() {
        String body = binding.noteBodyEditText.getText().toString();
        int index = body.indexOf(".~");
        Log.i(TAG, "getNewIndices: imageIndices.size (old)------------------------>" + imageIndices.size());
        imageIndices = new ArrayList<>();
        int i = 0;
        while (index >= 0) {
            imageIndices.add(i, String.valueOf(index + 2));
            i++;
            index = body.indexOf(".~", index + 1);
        }
        Log.i(TAG, "getNewIndices: imageIndices.size (new)------------------------>" + imageIndices.size());
        Log.i(TAG, "getNewIndices: imagePaths.size (old)------------------------>" + imagePaths.size());
        for (i = imageIndices.size(); i < imagePaths.size(); i++) {
            imagePaths.remove(i);
        }
        Log.i(TAG, "getNewIndices: imagePaths.size (new)------------------------>" + imagePaths.size());
    }

    private void openGallery() {
        imageNumber = imageIndices.size();
        Log.i(TAG, "openGallery: imageNumber------------------>" + imageNumber);
        if (imageNumber <= 2) {
            Intent getIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(getIntent, PICK_IMAGE);
        } else
            Toast.makeText(requireContext(), getString(R.string.max_images), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data.getData() != null) {
                Uri imageUri = data.getData();
                try {
                    Log.i(TAG, "onActivityResult: ---------------------");
                    InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    int imageIndex = binding.noteBodyEditText.getSelectionStart() + 5;
                    Log.i(TAG, "onActivityResult: update---------------------");
                    imageIndices.add(imageNumber, String.valueOf(imageIndex));
                    Log.i(TAG, "onActivityResult: after---------------------");
                    Utils.insertImageToCurrentSelection(bitmap, binding.noteBodyEditText);
                    Log.i(TAG, "onActivityResult: after1---------------------");
                    if (isExternalStorageAvailable()) {
                        saveFiles();
                    }
                    Log.i(TAG, "onActivityResult: after2---------------------");
                    imageNumber++;
                    Log.i(TAG, "onActivityResult: after3---------------------");
                } catch (Exception exception) {
                    Log.i(TAG, "onActivityResult: exception.message----------------------->" + exception.getMessage());
                }
            }
        }
    }

    private static boolean isExternalStorageAvailable() {
        boolean isAvailable = false;
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState) || Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void saveFiles() {
        File imagesFile = new File(requireActivity().getExternalFilesDir(null), FILENAME);
        imagesFile.mkdirs();
        String filename = String.valueOf(System.currentTimeMillis());
        File outFile = new File(imagesFile, filename);
        try {
            saveImageIntoInternalStorage(outFile, bitmap);
        } catch (IOException e) {
            Log.i(TAG, "Exception: IOException" + e.getMessage());
            Snackbar.make(binding.getRoot(), getString(R.string.error_in_save), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void saveImageIntoInternalStorage(File outFile, Bitmap bitmap) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(outFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
        Log.i(TAG, "saveImageIntoInternalStorage: before-------------------");
        Log.i(TAG, "saveImageIntoInternalStorage: imageNumber------------------>" + imageNumber);
        imagePaths.add(imageNumber, outFile.getAbsolutePath());
        Log.i(TAG, "saveImageIntoInternalStorage: after-------------------");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(() -> {

        });
    }


}