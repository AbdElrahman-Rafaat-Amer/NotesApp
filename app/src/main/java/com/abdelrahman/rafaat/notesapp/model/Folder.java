package com.abdelrahman.rafaat.notesapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "folders")
public class Folder {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;

    private String password;
    private boolean isPinned;
    private boolean isChecked;
    private int numberOfNotes;


    public Folder(String name) {
        this.name = name;
        this.password = "";
        this.isPinned = false;
        this.isChecked = false;
        this.numberOfNotes = 0;
    }

//    public Folder(String name, boolean isChecked) {
//        this.name = name;
//        this.password = "";
//        this.isPinned = false;
//        this.isChecked = isChecked;
//        this.numberOfNotes = 0;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getNumberOfNotes() {
        return numberOfNotes;
    }

    public void setNumberOfNotes(int numberOfNotes) {
        this.numberOfNotes = numberOfNotes;
    }


}
