package com.example.kitchen_assistant.models;

import android.os.Parcelable;
import android.util.Log;

import com.example.kitchen_assistant.clients.Spoonacular;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ParseClassName("Recipe")
public class Recipe extends ParseObject implements Parcelable {

    private static final String TAG = "RecipeModel";

    // Keyword for Parse columns
    public static final String KEY_ID = "objectId";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_NAME = "recipeName";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_IMAGE_URL = "imgUrl";
    public static final String KEY_INSTRUCTIONS = "instructions";
    public static final String KEY_CODE = "recipeCode";
    public static final String KEY_COOKABLE = "cookable";

    // Keyword for Spoonacular
    private static final String KEY_NAME_JSON_API = "title";
    private static final String KEY_INGREDIENTS_JSON_API = "missedIngredients";
    private static final String KEY_IMAGE_JSON_API = "image";
    private static final String KEY_ID_API = "id";

    // Local properties
    private String name;
    private String recipeCode;
    private String imageUrl;
    private String instructions;
    private boolean cookable;
    private HashMap<String, Ingredient> ingredients;

    public static Recipe extractFromJsonObject(JSONObject json) throws JSONException {
        Recipe result = new Recipe();
        result.setName(json.getString(KEY_NAME_JSON_API));
        result.setImageUrl(json.getString(KEY_IMAGE_JSON_API));
        result.setCode(json.getString(KEY_ID_API));
        result.setInstructions("no instructions");
        try {
            Spoonacular.getIngredients(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

    public void fetchInfo() {
        name = getString(KEY_NAME);
        imageUrl = getString(KEY_IMAGE_URL);
        instructions = getString(KEY_INSTRUCTIONS);
        recipeCode = getString(KEY_CODE);
        cookable = getBoolean(KEY_COOKABLE);
    }

    public void saveInfo() {
        put(KEY_NAME, name);
        put(KEY_IMAGE_URL, imageUrl);
        put(KEY_INSTRUCTIONS, instructions);
        put(KEY_CODE, recipeCode);
        put(KEY_COOKABLE, cookable);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imgUrl) {
        this.imageUrl = imgUrl;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getCode() {
        return recipeCode;
    }

    public void setCode(String code) {
        this.recipeCode = code;
    }

    public void setIngredients(HashMap<String, Ingredient> ingredientHashMap) {
        ingredients = ingredientHashMap;
    }

    public List<Ingredient> getIngredientList() {
        return new ArrayList<>(ingredients.values());
    }

    public boolean isCookable() {
        return cookable;
    }

    public void setCookable(boolean cookable) {
        this.cookable = cookable;
    }
}
