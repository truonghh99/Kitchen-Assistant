package com.example.kitchen_assistant.helpers;

import android.util.Log;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.clients.OpenFoodFacts;
import com.example.kitchen_assistant.fragments.CurrentFoodFragment;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentFoodTypes;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MatchingHelper {

    private static final String TAG = "MatchingHelper";

    // Match new product with existing product
    public static Product attemptToCreateProduct(String code) throws ParseException {
        Product product = new Product();
        if (code == CurrentFoodFragment.MANUALLY_INSERT_KEY) {
            product.setProductCode(code);
            return product;
        }
        if (CurrentProducts.productHashMap.containsKey(code)) { // Product exists in current list
            Log.e(TAG, "Product exists!");
            product = CurrentProducts.productHashMap.get(code);
        } else {
            Product scannedProduct = CurrentProducts.fetchProductWithCode(code);
            if (scannedProduct.getProductCode() != null) { // Product has been scanned
                Log.e(TAG, "Product has been scanned!");
                product = cloneProduct(scannedProduct);
            } else { // Product has never been scanned
                Log.e(TAG, "Look up new product");
                try {
                    product = OpenFoodFacts.getProductInfo(code);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return product;
    }

    private static Product cloneProduct(Product original) {
        Product product = new Product();
        product.setProductCode(original.getProductCode());
        product.setImageUrl(original.getImageUrl());
        product.setProductName(original.getProductName());
        product.setOriginalQuantity(original.getOriginalQuantity());
        product.setQuantityUnit(original.getQuantityUnit());
        product.setDuration(original.getDuration());
        product.setDurationUnit(original.getDurationUnit());

        product.setPurchaseDate(new Date());
        product.setNumProducts(1);
        product.updateCurrentQuantity();
        product.updateExpirationDate();
        product.updateFoodStatus();

        FoodItem foodItem = new FoodItem();
        FoodItem originalFoodItem = original.getFoodItem();
        originalFoodItem.fetchInfo();
        Log.e(TAG, originalFoodItem.getName());

        foodItem.setName(originalFoodItem.getName());
        foodItem.setQuantity(originalFoodItem.getQuantity());
        foodItem.setQuantityUnit(originalFoodItem.getQuantityUnit());

        attemptToAttachFoodItem(foodItem, product);
        return product;
    }

    // Match new item with existing item
    public static void attemptToAttachFoodItem(FoodItem food, Product product) {
        Log.e(TAG, "Start attaching");
        String name = food.getName();
        if (CurrentFoodTypes.foodItems.containsKey(name)) { // If such food type exists, increase quantity & handle the existing one
            Log.e(TAG, "Food type exists!");
            FoodItem foodItem = CurrentFoodTypes.foodItems.get(name);
            foodItem.increaseQuantity(food.getQuantity(), food.getQuantityUnit());
            CurrentFoodTypes.saveFoodItemInBackGround(foodItem);
            food = foodItem;
        } else {
            Log.e(TAG, "Created new food type!");
            CurrentFoodTypes.addFoodItem(food);
        }
        product.setFoodItem(food);
    }

    // Match new shopping item with existing item
    public static void attemptToCreateShoppingItem(String name, float quantity, String unit) {
        if (CurrentShoppingList.itemHashMap.containsKey(name)) { // If such item exists, increase quantity & handle the existing one
            Log.e(TAG, "Item exists!");
            ShoppingItem item = CurrentShoppingList.itemHashMap.get(name);
            item.increaseQuantity(quantity,unit);
            CurrentShoppingList.saveItemInBackGround(item);
        } else {
            ShoppingItem item = new ShoppingItem();
            item.setName(name);
            item.setQuantity(quantity);
            item.setQuantityUnit(unit);
            CurrentShoppingList.addItem(item);
            Log.e(TAG, "Created new item!");
        }
    }
}
