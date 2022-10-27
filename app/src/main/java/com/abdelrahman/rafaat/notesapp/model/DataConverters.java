package com.abdelrahman.rafaat.notesapp.model;

import android.text.SpannableString;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataConverters {
    @TypeConverter
    public String imagesPathListToString(ArrayList<String> imagesPath) {
        if (imagesPath == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.toJson(imagesPath, type);
    }

    @TypeConverter
    public ArrayList<String> imagesPathStringToList(String imagesPath) {
        if (imagesPath == null) {
            return new ArrayList();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(imagesPath, type);
    }

    @TypeConverter
    public String textFormatToString(TextFormat textFormat) {
        if (textFormat == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<TextFormat>() {
        }.getType();
        return gson.toJson(textFormat, type);
    }

    @TypeConverter
    public TextFormat stringToTextFormat(String textFormat) {
        if (textFormat == null) {
            return new TextFormat();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<TextFormat>() {
        }.getType();
        return gson.fromJson(textFormat, type);
    }
}
