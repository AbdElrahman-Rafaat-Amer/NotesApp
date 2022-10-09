package com.abdelrahman.rafaat.notesapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String body;
    private String date;
    private int color;
    private boolean isPinned;
    private String password;
    @TypeConverters(DataConverters.class)
    @ColumnInfo(name = "images_path")
    private ArrayList<String> imagePaths;

    @TypeConverters(DataConverters.class)
    @ColumnInfo(name = "images_indices")
    private ArrayList<String> imageIndices;

    public Note(String title, String body, String date, int color, ArrayList<String> imagePaths, ArrayList<String> imageIndices) {
        this.title = title;
        this.body = body;
        this.date = date;
        this.color = color;
        this.password = "";
        this.isPinned = false;
        this.imagePaths = imagePaths;
        this.imageIndices = imageIndices;
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

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public ArrayList<String> getImageIndices() {
        return imageIndices;
    }

    public void setImageIndices(ArrayList<String> imageIndices) {
        this.imageIndices = imageIndices;
    }
}
