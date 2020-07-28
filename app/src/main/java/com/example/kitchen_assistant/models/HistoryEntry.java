package com.example.kitchen_assistant.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("HistoryEntry")
public class HistoryEntry extends ParseObject {

    private static final String TAG = "HistoryEntry";

    // Parse key
    public static final String KEY_RECIPE_ID = "recipeId";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_CALORIES = "cumulativeCalories";
    public static final String KEY_CARBS = "cumulativeCarbs";
    public static final String KEY_PROTEIN = "cumulativeProtein";
    public static final String KEY_FAT = "cumulativeFat";

    // Local storage
    private String recipeId;
    private String userId;
    private Date timestamp;
    private float cumulativeCalories;
    private float cumulativeProtein;
    private float cumulativeCarbs;
    private float cumulativeFat;

    public void fetchInfo() {
        try {
            fetch();
        } catch (ParseException e) {
            Log.e(TAG, "Cannot fetch history entry");
            e.printStackTrace();
        }
        recipeId = getString(KEY_RECIPE_ID);
        userId = getString(KEY_USER_ID);
        timestamp = getDate(KEY_TIMESTAMP);
        cumulativeCalories = getNumber(KEY_CALORIES).floatValue();
        cumulativeCarbs = getNumber(KEY_CARBS).floatValue();
        cumulativeCalories = getNumber(KEY_PROTEIN).floatValue();
        cumulativeFat = getNumber(KEY_FAT).floatValue();
    }

    public void saveInfo() {
        put(KEY_RECIPE_ID, recipeId);
        put(KEY_USER_ID, userId);
        put(KEY_TIMESTAMP, timestamp);
        put(KEY_CALORIES, cumulativeCalories);
        put(KEY_CARBS, cumulativeCarbs);
        put(KEY_PROTEIN, cumulativeProtein);
        put(KEY_FAT, cumulativeFat);

    }
    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public float getCumulativeCalories() {
        return cumulativeCalories;
    }

    public void setCumulativeCalories(float cumulativeCalories) {
        this.cumulativeCalories = cumulativeCalories;
    }

    public float getCumulativeProtein() {
        return cumulativeProtein;
    }

    public void setCumulativeProtein(float cumulativeProtein) {
        this.cumulativeProtein = cumulativeProtein;
    }

    public float getCumulativeCarbs() {
        return cumulativeCarbs;
    }

    public void setCumulativeCarbs(float cumulativeCarbs) {
        this.cumulativeCarbs = cumulativeCarbs;
    }

    public float getCumulativeFat() {
        return cumulativeFat;
    }

    public void setCumulativeFat(float cumulativeFat) {
        this.cumulativeFat = cumulativeFat;
    }

}
