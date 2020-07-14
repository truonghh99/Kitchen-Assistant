package com.example.kitchen_assistant.storage;

import android.util.Log;
import android.widget.Toast;

import com.example.kitchen_assistant.models.Product;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class CurrentProducts {

    private static final String TAG = "CurrentProduct";
    public static List<Product> products = new ArrayList<>();

    private static final String KEY_ID = "objectId";
    private static final String KEY_CREATED_AT = "createdAt";
    private static final String KEY_CODE = "productCode";
    private static final String KEY_NAME = "productName";
    private static final String KEY_ORIGINAL_QUANTITY = "originalQuantiy";
    private static final String KEY_CURRENT_QUANTITY = "currentQuantity";
    private static final String KEY_QUANTITY_UNIT = "quantityUnit";
    private static final String KEY_NUM_PRODUCTS = "numProducts";
    private static final String KEY_IMG_URL = "imageUrl";
    private static final String KEY_PURCHASE_DATE = "purchaseDate";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_DURATION_UNIT = "durationUnit";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_FOOD_TYPE = "foodType";

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

    private static void saveProductInBackGround(Product product) {
        product.put(KEY_CODE, product.getProductCode());
        product.put(KEY_NAME, product.getProductName());
        product.put(KEY_ORIGINAL_QUANTITY, product.getOriginalQuantity());
        product.put(KEY_CURRENT_QUANTITY, product.getCurrentQuantity());
        product.put(KEY_QUANTITY_UNIT, product.getQuantityUnit());
        product.put(KEY_NUM_PRODUCTS, product.getNumProducts());
        product.put(KEY_IMG_URL, product.getImgUrl());
        product.put(KEY_PURCHASE_DATE, product.getPurchaseDate());
        product.put(KEY_DURATION, product.getDuration());
        product.put(KEY_DURATION_UNIT, product.getDurationUnit());
        //product.put(KEY_OWNER, product.getOwner());
        //product.put(KEY_FOOD_TYPE, product.getFoodItem());
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
}
