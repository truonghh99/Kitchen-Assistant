package com.example.kitchen_assistant.helpers;

import android.util.Log;

import com.example.kitchen_assistant.clients.OpenFoodFacts;
import com.example.kitchen_assistant.fragments.products.CurrentFoodFragment;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentFoodTypes;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return product;
    }

    private static Product cloneProduct(Product original) {
        // Get existing product's information
        Product product = new Product();
        product.setProductCode(original.getProductCode());
        product.setImageUrl(product.getImageUrl());
        product.setProductName(original.getProductName());
        product.setOriginalQuantity(original.getOriginalQuantity());
        product.setQuantityUnit(original.getQuantityUnit());
        product.setDuration(original.getDuration());
        product.setDurationUnit(original.getDurationUnit());

        // Assign values to new product
        product.setPurchaseDate(new Date());
        product.setNumProducts(1);
        product.updateCurrentQuantity();
        product.updateExpirationDate();
        product.updateFoodStatus();

        // Get existing product's food item
        FoodItem foodItem = new FoodItem();
        FoodItem originalFoodItem = original.getFoodItem();
        try {
            originalFoodItem.fetchInfo();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e(TAG, originalFoodItem.getName());

        // Assign values to new food item
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
            Log.i(TAG, "Food type exists!" + name);
            FoodItem foodItem = CurrentFoodTypes.foodItems.get(name);
            Log.i(TAG, "Old quantity: " + foodItem.getQuantity());
            foodItem.addQuantity(food.getQuantity(), food.getQuantityUnit());
            CurrentFoodTypes.saveFoodItemInBackGround(foodItem);
            food = foodItem;
            Log.i(TAG, "New quantity: " + foodItem.getQuantity());
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
