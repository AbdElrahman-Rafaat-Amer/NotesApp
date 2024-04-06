package com.abdelrahman.rafaat.notesapp.utils;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

public class SpannableTextBuilder {
    private final SpannableStringBuilder stringBuilder = new SpannableStringBuilder();

    public void append(String text) {
        stringBuilder.append(text != null ? text : "");
    }

    public void append(CharSequence charSequence) {
        stringBuilder.append(charSequence != null ? charSequence : "");
    }

    public void append(char c) {
        stringBuilder.append(c);
    }

    public void append(int number) {
        applySpan(null, "");
        stringBuilder.append(Integer.toString(number));
    }

    public void applySpan(Object span, String text) {
        int start = getPositionOfText(text);
        int end = text.length() + start;
        stringBuilder.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    private int getPositionOfText(String textToFind) {
        String fullText = stringBuilder.toString();
        return fullText.indexOf(textToFind);
    }

    public void appendAndApply(String text, Object... spans) {
        int start = stringBuilder.length();
        int end = text.length() + start;
        stringBuilder.append(text);
        for (Object span : spans) {
            stringBuilder.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    public CharSequence getSpannableText() {
        return stringBuilder;
    }
}
