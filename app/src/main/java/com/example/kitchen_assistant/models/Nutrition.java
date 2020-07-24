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
            Log.e(TAG, "Rating already existed");
            Nutrition result = nutritions.get(0);
            result.fetchInfo();
            recipe.setNutrition(result);
        }
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


}
