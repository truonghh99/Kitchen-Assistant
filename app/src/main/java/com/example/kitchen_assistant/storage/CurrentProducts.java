package com.example.kitchen_assistant.storage;

import android.util.Log;
import android.widget.Toast;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.fragments.CurrentProductFragment;
import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Product;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CurrentProducts {

    private static final String TAG = "CurrentProduct";
    public static List<Product> products;
    public static HashMap<String, Product> productHashMap;

    public static void addProduct(Product product) {
        products.add(0, product);
        CurrentProductFragment.notifyDataChange();
        productHashMap.put(product.getProductCode(), product);

        product.saveInfo();
        saveProductInBackGround(product);
    }

    public static void saveAllProducts() {
        Log.i(TAG, "Start saving product info");
        for (Product product : products) {
            saveProductInBackGround(product);
        }
    }

    public static void saveProductInBackGround(Product product) {
        product.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving product", e);
                    return;
                }
                Log.e(TAG,"Done");
            }
        });
    }

    public static void fetchProductInBackground() {
        Log.i(TAG, "Start querying for current products");

        products = new ArrayList<>();
        productHashMap = new HashMap<>();
        ParseQuery<Product> query = ParseQuery.getQuery(Product.class);
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Product>() {
            @Override
            public void done(List<Product> newProducts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying new posts");
                    return;
                }
                initialize(newProducts);
                CurrentProductFragment.notifyDataChange();
                Log.i(TAG, "Query completed, got " + products.size() + " products");
                MainActivity.hideProgressBar();
            }
        });
    }

    private static void initialize(List<Product> newProducts) {
        for (Product product : newProducts) {
            Log.e(TAG, "Got: " + product.getString("productName") + " - " + product.getCurrentQuantity());
            product.fetchInfo();
            Log.e(TAG, "Localized: " + product.getProductName() + " - " + product.getCurrentQuantity());
            products.add(product);
            productHashMap.put(product.getProductCode(), product);
        }
    }

    public static void removeProduct(Product product) {
        products.remove(product);
        //TODO: reduce quantity to 0 instead of removing
        ParseObject productParse = ParseObject.createWithoutData("Product", product.getObjectId());
        productParse.deleteEventually();
        CurrentProductFragment.notifyDataChange();
    }
}
