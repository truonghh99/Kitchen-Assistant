package com.example.kitchen_assistant.storage;

import android.util.Log;

import com.example.kitchen_assistant.fragments.CurrentProductFragment;
import com.example.kitchen_assistant.fragments.RecipeFragment;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Recipe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        if (foodItems.containsKey(foodItem.getString("foodName"))) return;
        Log.e(TAG, "Got: " + foodItem.getString("foodName") + " - " + foodItem.getNumber("quantity"));
        foodItem.fetchInfo();
        Log.e(TAG, "Localized: " + foodItem.getName() + " - " + foodItem.getQuantity());
        foodItems.put(foodItem.getName(), foodItem);
    }

}
