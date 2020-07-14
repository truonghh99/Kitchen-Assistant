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
import java.util.List;

public class CurrentFoodTypes {

    private static final String TAG = "CurrentFoodTypes";
    public static List<FoodItem> foodItems;

    public static void addFoodItem(FoodItem foodItem) {
        foodItems.add(foodItem);
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
                Log.e(TAG,"Done");
            }
        });
    }

    public static void fetchFoodItemInBackground() {
        Log.i(TAG, "Start querying for current food items");

        foodItems = new ArrayList<>();
        ParseQuery<FoodItem> query = ParseQuery.getQuery(FoodItem.class);
        query.addAscendingOrder("createdAt");

        query.findInBackground(new FindCallback<FoodItem>() {
            @Override
            public void done(List<FoodItem> newFoodItems, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying new posts");
                    return;
                }
                foodItems.addAll(newFoodItems);
                Log.i(TAG, "Query completed, got " + foodItems.size() + " recipes");
            }
        });
    }

}
