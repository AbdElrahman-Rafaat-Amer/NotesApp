package com.abdelrahman.rafaat.notesapp;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class Utils {
    private static final String TAG = "AddNoteFragment";

    public static void insertImageToCurrentSelection(Bitmap bitmap, EditText editText, int imageNumber) {

        BitmapDrawable drawable = setUpImage(bitmap);
        int selectionCursor = editText.getSelectionStart();
        String text = "\n\n\n~" + imageNumber;
        editText.getText().insert(selectionCursor, text);
        selectionCursor = editText.getSelectionStart();
        setUpBuilder(editText, drawable, selectionCursor);
        editText.setSelection(selectionCursor);
        editText.append("\n\n");
    }

    public static boolean insertImageToTextView(Bitmap bitmap, TextView textView, int selectionCursor) {
        boolean isSuccess = false;
        BitmapDrawable drawable = setUpImage(bitmap);
        if (drawable.getBitmap() != null) {
            textView.setText(textView.getText());
            setUpBuilder(textView, drawable, selectionCursor);
            isSuccess = true;
        }

        return isSuccess;
    }

    private static BitmapDrawable setUpImage(Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }

    private static void setUpBuilder(TextView textView, BitmapDrawable drawable, int selectionCursor) {
        SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText());
        builder.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_CENTER), selectionCursor - 2, selectionCursor,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
    }
}
