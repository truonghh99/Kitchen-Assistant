package com.example.kitchen_assistant.storage;

import android.util.Log;

import com.example.kitchen_assistant.fragments.CurrentProductFragment;
import com.example.kitchen_assistant.fragments.RecipeFragment;
import com.example.kitchen_assistant.fragments.ShoppingListFragment;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class CurrentShoppingList {

    private static final String TAG = "CurrentShoppingList";
    public static List<ShoppingItem> items;

    public static void addAllItems(List<ShoppingItem> shoppingItems) throws ParseException {
        for (ShoppingItem item : items) {
            Log.e(TAG, item.getName());
            addItem(item);
        }
        ShoppingListFragment.notifyDataChange();
    }
    public static void addItem(ShoppingItem item) {
        items.add(0, item);
        saveItemInBackGround(item);
        ShoppingListFragment.notifyDataChange();
    }
    public static void saveAllItems() {
        Log.e(TAG, "Start saving all shopping items");
        for (ShoppingItem item : items) {
            saveItemInBackGround(item);
        }
    }

    public static void saveItemInBackGround(ShoppingItem item) {
        item.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving shopping item", e);
                    return;
                }
                Log.e(TAG,"Succesfully saved shopping item");
            }
        });
    }

    public static void fetchItemsInBackground() {
        Log.i(TAG, "Start querying for current shopping items");

        items = new ArrayList<>();
        ParseQuery<ShoppingItem> query = ParseQuery.getQuery(ShoppingItem.class);
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<ShoppingItem>() {
            @Override
            public void done(List<ShoppingItem> newItems, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying new shopping items");
                    return;
                }
                items.addAll(newItems);
                ShoppingListFragment.notifyDataChange();
                Log.i(TAG, "Query completed, got " + items.size() + " recipes");
            }
        });
    }

    public static void removeItem(ShoppingItem item) {
        items.remove(item);
        ParseObject productParse = ParseObject.createWithoutData("ShoppingItem", item.getObjectId());
        productParse.deleteEventually();
        ShoppingListFragment.notifyDataChange();
    }

}
