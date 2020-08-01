package com.example.kitchen_assistant.storage;

import android.util.Log;

import com.example.kitchen_assistant.activities.MainActivity;
import com.example.kitchen_assistant.fragments.products.CurrentFoodFragment;
import com.example.kitchen_assistant.helpers.RecipeEvaluator;
import com.example.kitchen_assistant.models.Product;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CurrentProducts {

    private static final String TAG = "CurrentProduct";
    public static List<Product> products;
    public static HashMap<String, Product> productHashMap;

    public static void addProduct(Product product) {
        products.add(0, product);
        CurrentFoodFragment.notifyDataChange();
        productHashMap.put(product.getProductCode(), product);
        Log.e(TAG, "ADDED PRODUCT " + product.getProductCode() + " " + product.getProductName());

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
        product.saveInfo();
        Log.e(TAG, "SAVING CODE: " + product.getProductCode() + " " + product.getObjectId());
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
        MainActivity.showProgressBar();

        products = new ArrayList<>();
        productHashMap = new HashMap<>();
        ParseQuery<Product> query = ParseQuery.getQuery(Product.class);
        query.whereGreaterThan(Product.KEY_CURRENT_QUANTITY, 0);
        query.addDescendingOrder(Product.KEY_FOOD_STATUS);
        query.whereContains(Product.KEY_OWNER, ParseUser.getCurrentUser().getObjectId());
        query.include(Product.KEY_FOOD_TYPE);

        query.findInBackground(new FindCallback<Product>() {
            @Override
            public void done(List<Product> newProducts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying new posts");
                    return;
                }
                initialize(newProducts);
                CurrentFoodFragment.notifyDataChange();
                Log.i(TAG, "Query completed, got " + products.size() + " products");
                MainActivity.hideProgressBar();
            }
        });
    }

    public static Product fetchProductWithCode(final String code) throws ParseException {
        Log.e(TAG, "Start querying for product with code " + code);
        final boolean[] completed = {false};
        Product product = new Product();
        ParseQuery<Product> query = ParseQuery.getQuery(Product.class);
        query.whereEqualTo(Product.KEY_CODE, code);
        List<Product> results = query.find();
        Log.e(TAG, "Found " + results.size());
        if (results.size() > 0) {
            product = results.get(0);
            product.fetchInfo();
        }
        return product;
    }
    private static void initialize(List<Product> newProducts) {
        for (Product product : newProducts) {
            product.fetchInfo();
            if (!CurrentFoodTypes.foodItems.containsKey(product.getFoodItem().getName())) {
                CurrentFoodTypes.initialize(product.getFoodItem());
            }
            product.getFoodItem().addProductToType(product);
            products.add(product);
            productHashMap.put(product.getProductCode(), product);
        }
    }

    public static void removeProduct(Product product) {
        productHashMap.remove(product.getProductCode());
        products.remove(product);
        product.subtractQuantity(product.getCurrentQuantity(), product.getQuantityUnit());
        saveProductInBackGround(product);
        CurrentFoodTypes.saveFoodItemInBackGround(product.getFoodItem());
        RecipeEvaluator.evaluateAllRecipe();
        CurrentFoodFragment.notifyDataChange();
    }

    public static String getNameWithCode(String code) {
        Log.e(TAG, code);
        if (containsProduct(code)) {
            return productHashMap.get(code).getProductName();
        }
        return null;
    }

    public static boolean containsProduct(String code) {
        return productHashMap.containsKey(code);
    }

    public static Product getProductWithCode(String code) {
        return productHashMap.get(code);
    }

    public static int getCurrentNumProducts() {
        return products.size();
    }
}
