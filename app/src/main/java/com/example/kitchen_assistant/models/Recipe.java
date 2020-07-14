package com.example.kitchen_assistant.models;

import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Recipe")
public class Recipe extends ParseObject implements Parcelable {

    private static final String TAG = "RecipeModel";

    // Keyword for Parse columns
    private static final String KEY_ID = "objectId";
    private static final String KEY_CREATED_AT = "createdAt";
    private static final String KEY_NAME = "recipeName";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_IMAGE_URL = "imgUrl";
    private static final String KEY_INSTRUCTIONS = "instructions";

    // Keyword for Spoonacular
    private static final String KEY_NAME_JSON = "title";
    private static final String KEY_INGREDIENTS_JSON = "missedIngredients";
    private static final String KEY_IMAGE_JSON = "image";

    public static Recipe extractFromJsonObject(JSONObject json) throws JSONException {
        Recipe result = new Recipe();
        result.setName(json.getString(KEY_NAME_JSON));
        result.setImgUrl(json.getString(KEY_IMAGE_JSON));
        Log.i(TAG, result.getName() + ": " + result.getImageUrl());
        return result;
    }

    public static List<Recipe> extractFromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject json = jsonArray.getJSONObject(i);
            Recipe recipe = extractFromJsonObject(json);
            recipes.add(recipe);
        }
        return recipes;
    }

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
