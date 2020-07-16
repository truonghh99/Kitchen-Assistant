package com.example.kitchen_assistant.helpers;

import android.util.Log;

import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.storage.CurrentFoodTypes;
import com.parse.ParseUser;

public class MatchingHelper {

    private static final String TAG = "MatchingHelper";

    public static void attemptToAttachFoodItem(FoodItem toAdd, Product toAttach) {
        Log.e(TAG, "Start attaching");
        String name = toAdd.getName();
        if (CurrentFoodTypes.foodItems.containsKey(name)) { // If such food type exists, increase quantity & handle the existing one
            Log.e(TAG, "Food type exists!");
            FoodItem foodItem = CurrentFoodTypes.foodItems.get(name);
            foodItem.increaseQuantity(toAdd.getQuantity(), toAdd.getQuantityUnit());
            CurrentFoodTypes.saveFoodItemInBackGround(foodItem);
            toAdd = foodItem;
        } else {
            Log.e(TAG, "Created new food type!");
            CurrentFoodTypes.addFoodItem(toAdd);
        }
        toAttach.setFoodItem(toAdd);
    }

    public static void detachFoodItem(Product product) {
        Log.e(TAG, "Start detaching");
        FoodItem foodItem = product.getFoodItem();
        foodItem.increaseQuantity(product.getCurrentQuantity() * -1, product.getQuantityUnit());
    }
}
