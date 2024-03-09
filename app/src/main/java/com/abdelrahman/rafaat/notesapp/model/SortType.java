package com.abdelrahman.rafaat.notesapp.model;

public enum SortType {
    PINNED_NOTES("isPinned"),
    CREATION_DATE("date"),
    MODIFICATION_DATE("modificationDate"),
    TITLE("title"),
    LOCKED_NOTES("isLocked");

    private final String value;

    SortType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
