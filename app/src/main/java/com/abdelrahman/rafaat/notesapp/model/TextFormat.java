package com.abdelrahman.rafaat.notesapp.model;

import android.view.Gravity;

public class TextFormat {
    private int textSize = 18;
    private int textAlignment = Gravity.TOP | Gravity.START;

    public TextFormat(int textSize, int textAlignment) {
        this.textSize = textSize;
        this.textAlignment = textAlignment;
    }

    public TextFormat() {
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextAlignment() {
        return textAlignment;
    }

    public void setTextAlignment(int textAlignment) {
        this.textAlignment = textAlignment;
    }
}
