package com.example.kitchen_assistant.storage;

import android.util.Log;

import com.example.kitchen_assistant.fragments.ShoppingListFragment;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CurrentShoppingList {

    private static final String TAG = "CurrentShoppingList";
    public static List<ShoppingItem> items;
    public static HashMap<String, ShoppingItem> itemHashMap;

    public static void addAllItems(List<ShoppingItem> shoppingItems) throws ParseException {
        for (ShoppingItem item : items) {
            Log.e(TAG, item.getName());
            addItem(item);
        }
        ShoppingListFragment.notifyDataChange();
    }

    public static void addItem(ShoppingItem item) {
        item.saveInfo();
        items.add(0, item);
        itemHashMap.put(item.getName(), item);
        saveItemInBackGround(item);
        ShoppingListFragment.notifyDataChange();
    }

    public static void saveAllItems() {
        for (ShoppingItem item : items) {
            saveItemInBackGround(item);
        }
    }

    public static void saveItemInBackGround(ShoppingItem item) {
        Log.e(TAG, "Start saving shopping items");
        item.saveInfo();
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
        itemHashMap = new HashMap<>();
        ParseQuery<ShoppingItem> query = ParseQuery.getQuery(ShoppingItem.class);
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<ShoppingItem>() {
            @Override
            public void done(List<ShoppingItem> newItems, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying new shopping items");
                    return;
                }
                initialize(newItems);
                ShoppingListFragment.notifyDataChange();
                Log.i(TAG, "Query completed, got " + items.size() + " shopping items");
            }
        });
    }

    private static void initialize(List<ShoppingItem> newItems) {
        for (ShoppingItem item : newItems) {
            item.fetchInfo();
            items.add(item);
            itemHashMap.put(item.getName(), item);
        }
    }

    public static void removeItem(ShoppingItem item) {
        items.remove(item);
        itemHashMap.remove(item);
        ParseObject productParse = ParseObject.createWithoutData("ShoppingItem", item.getObjectId());
        productParse.deleteEventually();
        ShoppingListFragment.notifyDataChange();
    }

}
