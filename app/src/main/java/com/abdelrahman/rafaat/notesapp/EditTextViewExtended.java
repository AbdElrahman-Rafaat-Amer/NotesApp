package com.abdelrahman.rafaat.notesapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.widget.EditText;

public class EditTextViewExtended {

    public static void insertImageToCurrentSelection(Bitmap Bitmap, EditText editText) {
        BitmapDrawable drawable = new BitmapDrawable(Resources.getSystem(), Bitmap);
        drawable.setBounds(0, 0, editText.getRight(), drawable.getIntrinsicHeight());

        int selectionCursor = editText.getSelectionStart();
        editText.getText().insert(selectionCursor, "\n\n.");
        selectionCursor = editText.getSelectionStart();

        SpannableStringBuilder builder = new SpannableStringBuilder(editText.getText());
        builder.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE), selectionCursor - ".".length(), selectionCursor,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setText(builder);
        editText.setSelection(selectionCursor);
        editText.append("\n\n");
    }
}
