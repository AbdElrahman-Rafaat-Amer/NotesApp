package com.abdelrahman.rafaat.notesapp.model;

import java.util.Objects;

public enum PasswordsTime {
    ONCE_PER_APP("-1"),
    EVERY_TIME_OPEN_SCREEN("0"),
    EVERY_5_MINUTES("5"),
    EVERY_10_MINUTES("10"),
    CUSTOM_TIME("1");


    private final String  value;

    PasswordsTime(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PasswordsTime fromValue(String value) {
        for (PasswordsTime time : PasswordsTime.values()) {
            if (Objects.equals(time.getValue(), value)) {
                return time;
            }
        }
        return null;
    }
}
