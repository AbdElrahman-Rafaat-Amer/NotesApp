package com.abdelrahman.rafaat.notesapp.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.widget.EditText;

import com.abdelrahman.rafaat.notesapp.R;

import java.util.Locale;

public class Utils {

    private Utils() {
    }

    public static void insertImageToCurrentSelection(Bitmap bitmap, EditText editText, String source) {
        BitmapDrawable drawable = setUpImage(bitmap);
        int selectionCursor = editText.getSelectionStart();
        String text = "\n\n\n.";
        editText.getText().insert(selectionCursor, text);
        selectionCursor = editText.getSelectionStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            editText.getText().setSpan(new ImageSpan(drawable, source, ImageSpan.ALIGN_CENTER),
                    selectionCursor - 1, selectionCursor, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            editText.getText().setSpan(new ImageSpan(drawable, source, ImageSpan.ALIGN_BASELINE),
                    selectionCursor - 1, selectionCursor, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        editText.setSelection(selectionCursor);
        editText.getText().insert(selectionCursor, "\n\n");
    }

    private static BitmapDrawable setUpImage(Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }

    public static boolean isRTL() {
        String language = Locale.getDefault().getDisplayName();
        int directionality = Character.getDirectionality(language.charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }
}
