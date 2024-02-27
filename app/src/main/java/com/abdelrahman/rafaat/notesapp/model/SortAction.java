package com.abdelrahman.rafaat.notesapp.model;

import androidx.annotation.Nullable;

public class SortAction {
    private SortOrder sortOrder = SortOrder.DESC;
    private SortType sortType = SortType.PINNED_NOTES;

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(boolean isDescending) {
        if (isDescending) {
            this.sortOrder = SortOrder.DESC;
        } else {
            this.sortOrder = SortOrder.ASC;
        }
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean isEqual = false;

        if (obj != null && obj.getClass() == getClass()) {
            //obj not null and the same type so will cast it
            if (this == obj) {
                isEqual = true;
            }else {
                SortAction other = (SortAction) obj;
                isEqual = sortOrder == other.sortOrder && sortType == other.sortType;
            }
        }
        return isEqual;
    }
}