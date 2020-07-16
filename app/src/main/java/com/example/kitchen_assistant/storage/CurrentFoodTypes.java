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
    public static HashMap<String, FoodItem> foodItems;

    public static void addFoodItem(FoodItem foodItem) {
        foodItem.saveInfo();
        foodItems.put(foodItem.getName(), foodItem);
        saveFoodItemInBackGround(foodItem);
    }

    public static void saveFoodItemInBackGround(FoodItem foodItem) {
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

    public static void fetchFoodItemInBackground() {
        Log.i(TAG, "Start querying for current food items");

        foodItems = new HashMap<>();
        ParseQuery<FoodItem> query = ParseQuery.getQuery(FoodItem.class);
        query.addAscendingOrder("createdAt");

        query.findInBackground(new FindCallback<FoodItem>() {
            @Override
            public void done(List<FoodItem> newFoodItems, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying new posts");
                    return;
                }
                initalize(newFoodItems);
                for (FoodItem foodItem : newFoodItems) {
                    foodItem.fetchInfo();
                    addFoodItem(foodItem);
                }
                Log.i(TAG, "Query completed, got " + foodItems.size() + " food items");
            }
        });
    }

    private static void initalize(List<FoodItem> newFoodItems) {
        for (FoodItem foodItem : newFoodItems) {
            foodItem.fetchInfo();
            foodItems.put(foodItem.getName(), foodItem);
        }
    }

}
