package com.example.kitchen_assistant.models;

import android.os.Parcelable;
import android.util.Log;

import com.example.kitchen_assistant.clients.Spoonacular;
import com.example.kitchen_assistant.helpers.MetricConverter;
import com.parse.CountCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

@ParseClassName("Nutrition")
public class Nutrition extends ParseObject implements Parcelable {

    private static final String TAG = "Nutrition";

    // Keys for Parse
    public static final String KEY_RECIPE_ID = "recipeId";
    public static final String KEY_CALORIES = "calories";
    public static final String KEY_CARBS = "carbs";
    public static final String KEY_FAT = "fat";
    public static final String KEY_PROTEIN = "protein";

    // Keys for Spoonacular
    public static final String API_KEY_CALORIES = "calories";
    public static final String API_KEY_CARBS = "carbs";
    public static final String API_KEY_PROTEIN = "protein";
    public static final String API_KEY_FAT = "fat";

    // Local values
    private String recipeId;
    private Float calories;
    private Float carbs;
    private Float fat;

    private Float protein;

    public static void requestNutrition(Recipe recipe) throws ParseException {
        ParseQuery<Nutrition> query = ParseQuery.getQuery(Nutrition.class);
        query.whereEqualTo(KEY_RECIPE_ID, recipe.getCode());
        List<Nutrition> nutritions = query.find();
        if (nutritions.size() == 0) {
            Log.e(TAG, "Creating new nutrition object");
            Spoonacular.getNutrition(recipe);
        } else {
            Log.e(TAG, "nutrition already existed");
            Nutrition result = nutritions.get(0);
            result.fetchInfo();
            recipe.setNutrition(result);
            result.saveInfo();
        }
    }

    public static Nutrition extractNutritionFromJson(JSONObject jsonObject, Recipe recipe) throws JSONException {
        Nutrition result = new Nutrition();

        Float calories = MetricConverter.extractQuantityVal(jsonObject.getString(API_KEY_CALORIES));
        Float carbs = MetricConverter.extractQuantityVal(jsonObject.getString(API_KEY_CARBS));
        Float protein = MetricConverter.extractQuantityVal(jsonObject.getString(API_KEY_PROTEIN));
        Float fat = MetricConverter.extractQuantityVal(jsonObject.getString(API_KEY_FAT));

        result.setRecipeId(recipe.getCode());
        result.setCarbs(carbs);
        result.setCalories(calories);
        result.setProtein(protein);
        result.setFat(fat);

        return result;
    }

    public static void requestManualNutrition(Recipe recipe) {
        Nutrition result = new Nutrition();

        // TODO: ALLOW USER TO CUSTOMIZE THESE NUTRITION VALUES
        result.setRecipeId(recipe.getCode());
        result.setCalories(500f);
        result.setCarbs(50f);
        result.setProtein(20f);
        result.setFat(5f);
        result.saveInfo();

        recipe.setNutrition(result);
    }

    public void fetchInfo() {
        try {
            fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        recipeId = getString(KEY_RECIPE_ID);
        calories = getNumber(KEY_CALORIES).floatValue();
        carbs = getNumber(KEY_CARBS).floatValue();
        protein = getNumber(KEY_PROTEIN).floatValue();
        fat = getNumber(KEY_FAT).floatValue();
    }

    public void saveInfo() {
        put(KEY_RECIPE_ID, recipeId);
        put(KEY_CALORIES, calories);
        put(KEY_CARBS, carbs);
        put(KEY_PROTEIN, protein);
        put(KEY_FAT, fat);
        saveInBackground();
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public Float getCalories() {
        return calories;
    }

    public void setCalories(Float calories) {
        this.calories = calories;
    }

    public Float getCarbs() {
        return carbs;
    }

    public void setCarbs(Float carbs) {
        this.carbs = carbs;
    }

    public Float getFat() {
        return fat;
    }

    public void setFat(Float fat) {
        this.fat = fat;
    }

    public Float getProtein() {
        return protein;
    }

    public void setProtein(Float protein) {
        this.protein = protein;
    }
}
