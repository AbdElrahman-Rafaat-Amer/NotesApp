package com.abdelrahman.rafaat.notesapp.model;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "notes")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String body;
    private String date;
    private int color;
    private boolean isPinned;
    private String password;
    @TypeConverters(DataConverters.class)
    private TextFormat textFormat;

    public Note(String title, String body, String date, int color, TextFormat textFormat) {
        this.title = title;
        this.body = body;
        this.date = date;
        this.color = color;
        this.password = "";
        this.isPinned = false;
        this.textFormat = textFormat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TextFormat getTextFormat() {
        return textFormat;
    }

    public void setTextFormat(TextFormat textFormat) {
        this.textFormat = textFormat;
    }

}
