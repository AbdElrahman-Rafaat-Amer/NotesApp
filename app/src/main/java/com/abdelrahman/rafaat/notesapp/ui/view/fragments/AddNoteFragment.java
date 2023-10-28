package com.abdelrahman.rafaat.notesapp.ui.view.fragments;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.abdelrahman.rafaat.notesapp.R;
import com.abdelrahman.rafaat.notesapp.databinding.FragmentAddNoteBinding;
import com.abdelrahman.rafaat.notesapp.model.Note;
import com.abdelrahman.rafaat.notesapp.ui.viewmodel.NoteViewModel;
import com.abdelrahman.rafaat.notesapp.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddNoteFragment extends BaseFragment {

//    private static final int PICK_IMAGE = 1;
    private FragmentAddNoteBinding binding;
    private NoteViewModel noteViewModel;
    private boolean isTextChanged = false;
    private Note note;
    private int noteColor = -1;
    private boolean isUpdate = false;
    private boolean isKeyboardVisible = false;
    private boolean isBodyHasFocus = false;
    private String imagePath;
    private Bitmap bitmap;
    private int textSize = 18;
    private int textAlignment = Gravity.TOP | Gravity.START;
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isUnderLine = false;
    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(5), uris -> {
                if (!uris.isEmpty()) {
                    for (Uri imageUri : uris) {
                        try {
                            InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            if (isExternalStorageAvailable()) {
                                prepareFile();
                            }
                            Utils.insertImageToCurrentSelection(bitmap, binding.noteBodyEditText, imagePath);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                } else {
                    showSnackBar(binding.rootView, getString(R.string.select_one_image_at_least));
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddNoteBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();
        initUI();
        checkIsEdit();
        checkRTL();
        watchText();
        watchKeyboard();
        onBackPressed();

    }

    private void initViewModel() {
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
    }

    private void initUI() {
        binding.saveImageView.setOnClickListener(v -> {
            if (checkTitle() & checkBody()) {
                if (isUpdate)
                    updateNote();
                else
                    saveNote();
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

        binding.closeTextPickerView.setOnClickListener(v -> {
            binding.helperBar.setVisibility(View.VISIBLE);
            binding.textBar.setVisibility(View.GONE);
        });

        binding.goBackImageView.setOnClickListener(v -> {
            if (isTextChanged) {
                if (isUpdate)
                    addDialogNote(getString(R.string.save_changes));
                else
                    addDialogNote(getString(R.string.discard_note));
            } else
                Navigation.findNavController(requireView()).popBackStack();
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
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
        binding.helperBar.setVisibility(View.INVISIBLE);

        binding.bold.setOnClickListener(v -> {
            isBold = !isBold;
            if (isBold)
                binding.bold.setBackgroundColor(getResources().getColor(R.color.mainColor, null));
            else
                binding.bold.setBackgroundColor(getResources().getColor(R.color.transparent, null));
        });

        binding.italic.setOnClickListener(v -> {
            isItalic = !isItalic;
            if (isItalic)
                binding.italic.setBackgroundColor(getResources().getColor(R.color.mainColor, null));
            else
                binding.italic.setBackgroundColor(getResources().getColor(R.color.transparent, null));
        });

        binding.underline.setOnClickListener(v -> {
            isUnderLine = !isUnderLine;
            if (isUnderLine)
                binding.underline.setBackgroundColor(getResources().getColor(R.color.mainColor, null));
            else
                binding.underline.setBackgroundColor(getResources().getColor(R.color.transparent, null));
        });

        binding.left.setOnClickListener(v -> setTextAlignment("left"));

        binding.center.setOnClickListener(v -> setTextAlignment("center"));

        binding.right.setOnClickListener(v -> setTextAlignment("right"));

        binding.smallFont.setOnClickListener(v -> {
            if (textSize > 18) {
                setFontSize(-2);
            } else {
                binding.smallFont.setImageResource(R.drawable.ic_text_font_decrease_deactived);
            }
        });

        binding.bigFont.setOnClickListener(v -> {
            if (textSize < 30) {
                setFontSize(2);
            } else {
                binding.bigFont.setImageResource(R.drawable.ic_text_font_increase_deactive);
            }
        });
    }

    private void setTextAlignment(String alignment) {
        switch (alignment) {
            case "left":
                textAlignment = Gravity.START;
                binding.left.setBackgroundColor(getResources().getColor(R.color.mainColor, null));
                binding.center.setBackgroundColor(getResources().getColor(R.color.transparent, null));
                binding.right.setBackgroundColor(getResources().getColor(R.color.transparent, null));
                break;

            case "center":
                textAlignment = Gravity.CENTER_HORIZONTAL;
                binding.left.setBackgroundColor(getResources().getColor(R.color.transparent, null));
                binding.center.setBackgroundColor(getResources().getColor(R.color.mainColor, null));
                binding.right.setBackgroundColor(getResources().getColor(R.color.transparent, null));
                break;

            case "right":
                textAlignment = Gravity.END;
                binding.left.setBackgroundColor(getResources().getColor(R.color.transparent, null));
                binding.center.setBackgroundColor(getResources().getColor(R.color.transparent, null));
                binding.right.setBackgroundColor(getResources().getColor(R.color.mainColor, null));
                break;
        }
        binding.noteBodyEditText.setGravity(textAlignment);
    }

    private void setFontSize(int amount) {
        textSize += amount;
        binding.noteBodyEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        binding.smallFont.setImageResource(R.drawable.ic_text_font_decrease);
        binding.bigFont.setImageResource(R.drawable.ic_text_font_increase);
    }

    private void checkIsEdit() {
        try {
            note = noteViewModel.getCurrentNote();
            binding.noteTitleEditText.setText(note.getTitle());
            Html.ImageGetter getter = source -> {
                Bitmap bitmap = BitmapFactory.decodeFile(source);
                BitmapDrawable drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            };
            Spanned noteBody = Html.fromHtml(note.getBody(), Html.FROM_HTML_MODE_LEGACY, getter, null);
            binding.noteBodyEditText.setText(noteBody);
            noteColor = note.getColor();
            textSize = note.getTextSize();
            textAlignment = note.getTextAlignment();
            binding.noteBodyEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            binding.noteBodyEditText.setGravity(textAlignment);
            isUpdate = true;

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
            exception.printStackTrace();
        }
    }

    private void checkRTL() {
        String language = Locale.getDefault().getDisplayName();
        int directionality = Character.getDirectionality(language.charAt(0));
        boolean isRTL = directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
        if (isRTL)
            binding.goBackImageView.setImageResource(R.drawable.ic_arrow_right);
    }

    private void chooseColor() {
        binding.colorPickerView.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedOption = binding.colorPickerView.getCheckedRadioButtonId();

            RadioButton radioButton = binding.getRoot().findViewById(selectedOption);//requireView().findViewById(selectedOption);
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
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isBold) {
                    binding.noteBodyEditText.getText().setSpan(new android.text.style.StyleSpan(Typeface.BOLD), start, start + count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (isItalic) {
                    binding.noteBodyEditText.getText().setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), start, start + count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (isUnderLine) {
                    binding.noteBodyEditText.getText().setSpan(new UnderlineSpan(), start, start + count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                isTextChanged = true;
            }
        });
    }

    private void onBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isTextChanged & checkTitle() & checkBody()) {
                    if (isUpdate)
                        addDialogNote(getString(R.string.save_changes));
                    else
                        addDialogNote(getString(R.string.discard_note));
                } else
                    Navigation.findNavController(requireView()).popBackStack();
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
        Window window = requireActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        Objects.requireNonNull(alertDialog.getWindow()).setLayout(
                (int) (displayRectangle.width() * 0.82f),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogMessage.setText(message);

        saveButton.setOnClickListener(v -> {
            if (checkTitle() & checkBody()) {
                if (isUpdate)
                    updateNote();
                else
                    saveNote();
            }
            alertDialog.dismiss();
        });

        discardButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            Navigation.findNavController(binding.getRoot()).popBackStack();
        });

    }

    private boolean checkTitle() {
        boolean isTitleTrue = false;
        if (binding.noteTitleEditText.getText().toString().trim().isEmpty())
            binding.noteTitleEditText.setError(getText(R.string.title_error));
        else
            isTitleTrue = true;

        return isTitleTrue;
    }

    private boolean checkBody() {
        boolean isBodyTrue = false;
        if (binding.noteBodyEditText.getText().toString().trim().isEmpty())
            binding.noteBodyEditText.setError(getText(R.string.body_error));
        else
            isBodyTrue = true;

        return isBodyTrue;
    }

    private void saveNote() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MM yyy HH:mm a", Locale.getDefault());
        Note note = new Note(binding.noteTitleEditText.getText().toString(),
                Html.toHtml(binding.noteBodyEditText.getText(), Html.FROM_HTML_MODE_LEGACY),
                formatter.format(new Date()), noteColor,
                textSize, textAlignment);

        noteViewModel.saveNote(note);
        Navigation.findNavController(requireView()).popBackStack();
    }

    private void updateNote() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MM yyy HH:mm a", Locale.getDefault());
        note.setTitle(binding.noteTitleEditText.getText().toString());
        note.setBody(Html.toHtml(binding.noteBodyEditText.getText(), Html.FROM_HTML_MODE_LEGACY));
        note.setDate(formatter.format(new Date()));
        note.setColor(noteColor);
        note.setTextSize(textSize);
        note.setTextAlignment(textAlignment);
        noteViewModel.updateNote(note);
        Navigation.findNavController(requireView()).popBackStack();
    }

    private void openGallery() {
        pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private static boolean isExternalStorageAvailable() {
        boolean isAvailable = false;
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState) || Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void prepareFile() {
        File imagesFile = new File(requireActivity().getExternalFilesDir(null), "images");
        imagesFile.mkdirs();
        String filename = String.valueOf(System.currentTimeMillis());
        File outFile = new File(imagesFile, filename);
        try {
            saveImageIntoInternalStorage(outFile, bitmap);
        } catch (IOException e) {
            Snackbar.make(binding.getRoot(), getString(R.string.error_in_save), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void saveImageIntoInternalStorage(File outFile, Bitmap bitmap) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(outFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
        imagePath = outFile.getAbsolutePath();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(() -> {
        });
    }
}