package com.abdelrahman.rafaat.notesapp.ui.view;

public enum ViewTypes {
    VIEW_TYPE_ITEM_1(1),
    VIEW_TYPE_ITEM_2(2);

    private int index;

    ViewTypes(int i) {
        index = i;
    }

    public int getValue() {
        return index;
    }
}
