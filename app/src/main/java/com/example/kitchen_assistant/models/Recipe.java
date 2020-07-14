package com.example.kitchen_assistant.models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("Recipe")
public class Recipe extends ParseObject implements Parcelable {

    // Keyword for Parse columns
    private static final String KEY_ID = "objectId";
    private static final String KEY_CREATED_AT = "createdAt";
    private static final String KEY_NAME = "recipeName";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_IMAGE_URL = "imgUrl";
    private static final String KEY_INSTRUCTIONS = "instructions";

    public void setName(String name) {
        put(KEY_NAME, name);
    }
    public void setAuthor(ParseUser author) {
        put(KEY_AUTHOR, author);
    }
    public void setImgUrl(String imgUrl) {
        put(KEY_IMAGE_URL, imgUrl);
    }
    public void setInstructions(String instructions) {
        put(KEY_INSTRUCTIONS, instructions);
    }
    public String getName() {
        return getString(KEY_NAME);
    }
    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }
    public String getImageUrl() {
        return getString(KEY_IMAGE_URL);
    }
    public String getInstructions() {
        return getString(KEY_INSTRUCTIONS);
    }
}
