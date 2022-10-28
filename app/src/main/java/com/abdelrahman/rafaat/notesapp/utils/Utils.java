package com.abdelrahman.rafaat.notesapp.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.abdelrahman.rafaat.notesapp.model.Note;

public class Utils {

    public static void insertImageToCurrentSelection(Bitmap bitmap, EditText editText, String source) {
        BitmapDrawable drawable = setUpImage(bitmap);
        int selectionCursor = editText.getSelectionStart();
        String text = "\n\n\n.";
        editText.getText().insert(selectionCursor, text);
        selectionCursor = editText.getSelectionStart();
        editText.getText().setSpan(new ImageSpan(drawable, source, ImageSpan.ALIGN_CENTER),
                selectionCursor - 1, selectionCursor, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setSelection(selectionCursor);
        editText.getText().insert(selectionCursor, "\n\n");
    }

    private static BitmapDrawable setUpImage(Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }

}
