package com.example.kitchen_assistant.helpers;

import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.adapters.CurrentFoodAdapter;
import com.example.kitchen_assistant.clients.OpenFoodFacts;
import com.example.kitchen_assistant.fragments.CurrentProductFragment;
import com.example.kitchen_assistant.fragments.NewProductDetailFragment;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.example.kitchen_assistant.storage.CurrentFoodTypes;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.example.kitchen_assistant.storage.CurrentShoppingList;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.IOException;

public class MatchingHelper {

    private static final String TAG = "MatchingHelper";

    // Match new product with existing product
    public static Product attemptToCreateProduct(String code) {
        Product product = new Product();
        if (code == CurrentProductFragment.MANUALLY_INSERT_KEY) {
            product.setProductCode(code);
            return product;
        }
        if (CurrentProducts.productHashMap.containsKey(code)) {
            Log.e(TAG, "Product exists!");
            product = CurrentProducts.productHashMap.get(code);
        } else {
            Log.e(TAG, "Create new product");
            try {
                product = OpenFoodFacts.getProductInfo(code);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return product;
    }

    // Match new item with existing item
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
