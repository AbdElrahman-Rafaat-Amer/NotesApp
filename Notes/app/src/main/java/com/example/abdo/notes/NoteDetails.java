package com.example.abdo.notes;

public class NoteDetails {
    private String noteTitle, noteContent;
    private int id;

    public NoteDetails(String noteTitle, String noteContent, int id) {
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.id = id;
    }

    public NoteDetails(String noteTitle, String noteContent) {
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
