package com.example.kitchen_assistant.storage;

import android.util.Log;

import com.example.kitchen_assistant.models.FoodItem;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.HashMap;

public class CurrentFoodTypes {

    private static final String TAG = "CurrentFoodTypes";
    public static HashMap<String, FoodItem> foodItems = new HashMap<>();

    public static void addFoodItem(FoodItem foodItem) {
        foodItem.saveInfo();
        foodItems.put(foodItem.getName(), foodItem);
        saveFoodItemInBackGround(foodItem);
    }

    public static void saveFoodItemInBackGround(FoodItem foodItem) {
        foodItem.saveInfo();
        foodItem.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving food item", e);
                    return;
                }
                Log.i(TAG,"Saved food item to Parse");
            }
        });
    }

    public static void initialize(FoodItem foodItem) {
        try {
            foodItem.fetchInfo();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        foodItems.put(foodItem.getName(), foodItem);
    }

    public static void removeFoodType(FoodItem foodItem) {
        foodItem.remove(foodItem.getName());
        foodItem.deleteInBackground();
    }
}
