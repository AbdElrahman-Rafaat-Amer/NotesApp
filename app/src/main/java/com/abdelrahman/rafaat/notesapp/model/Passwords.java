package com.abdelrahman.rafaat.notesapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "passwords")
public class Passwords implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String userName;
    private String password;
    private String websiteName;
    private int icon;

    public Passwords(String userName, String password, String websiteName, int icon) {
        this.userName = userName;
        this.password = password;
        this.websiteName = websiteName;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
