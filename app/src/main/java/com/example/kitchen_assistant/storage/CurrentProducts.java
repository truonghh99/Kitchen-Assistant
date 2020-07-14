package com.example.kitchen_assistant.storage;

import android.util.Log;
import android.widget.Toast;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.fragments.CurrentFoodFragment;
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
import java.util.List;

public class CurrentProducts {

    private static final String TAG = "CurrentProduct";
    public static List<Product> products;

    public static void addProduct(Product product) {
        products.add(product);
        saveProductInBackGround(product);
    }
    public static void saveAllProducts() {
        Log.e(TAG, "Start saving");
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
        ParseQuery<Product> query = ParseQuery.getQuery(Product.class);
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Product>() {
            @Override
            public void done(List<Product> newProducts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying new posts");
                    return;
                }
                products.addAll(newProducts);
                CurrentFoodFragment.notifyDataChange();
                Log.i(TAG, "Query completed, got " + products.size() + " products");
            }
        });
    }

    public static void removeProduct(Product product) {
        products.remove(product);
        ParseObject productParse = ParseObject.createWithoutData("Product", product.getObjectId());
        productParse.deleteEventually();
        CurrentFoodFragment.notifyDataChange();
    }
}
