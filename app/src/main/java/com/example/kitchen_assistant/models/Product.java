package com.example.kitchen_assistant.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Parcel
public class Product {

    private static final String TAG = "product";
    private static final String DEFAULT_IMG = "https://cdn.dribbble.com/users/67525/screenshots/4517042/agarey_grocerydribbble.png";

    private static final String PRODUCT_INFO = "product";
    private static final String BARCODE = "code";
    private static final String NAME = "product_name";
    private static final String QUANTITY = "quantity";
    private static final String IMAGE_URL = "image_thumb_url";

    private String productId;
    private String productName;
    private int quantity;
    private String quantityUnit;
    private String imgUrl;
    private Date expirationDate;

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    private Date purchaseDate;
    private String foodStatus;
    private User owner;
    private FoodItem foodItem;

    public Product () {
    }

    public Product (JSONObject json) throws JSONException, ParseException {
        Log.e(TAG, "START INIT");
        JSONObject product = json.getJSONObject(PRODUCT_INFO);

        productId = json.getString(BARCODE);
        productName = product.getString(NAME);
        try {
            String quantityStr = product.getString(QUANTITY);
            quantity = extractQuantityVal(quantityStr);
            quantityUnit = extractQuantityUnit(quantityStr);
        } catch (JSONException e) {
            quantity = 0;
            quantityUnit = null;
        }
        try {
            imgUrl = product.getString(IMAGE_URL);
        } catch (JSONException e) {
            imgUrl = DEFAULT_IMG;
        }
        purchaseDate = new Date();

        Log.e(TAG, productId);
        Log.e(TAG, productName);
        Log.e(TAG, "" + quantity + " " + quantityUnit);
        Log.e(TAG, imgUrl);
        Log.e(TAG, String.valueOf(purchaseDate));
    }

    private String extractQuantityUnit(String quantityStr) {
        if (quantityStr.isEmpty()) {
            return null;
        }
        int i = 0;
        while (quantityStr.charAt(i) <= '9' && quantityStr.charAt(i) >= '0') ++i;
        ++i;
        String unit = "";
        while (i < quantityStr.length() && quantityStr.charAt(i) != ' ') {
            unit += quantityStr.charAt(i);
            ++i;
        }
        return unit;
    }

    private int extractQuantityVal(String quantityStr) {
        if (quantityStr.isEmpty()) {
            return 0;
        }
        int quantityVal = 0;
        for (int i = 0; quantityStr.charAt(i) <= '9' && quantityStr.charAt(i) >= '0'; ++i) {
            quantityVal = quantityVal * 10 + (int) (quantityStr.charAt(i) - '0');
        }
        return quantityVal;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getFoodStatus() {
        return foodStatus;
    }

    public void setFoodStatus(String foodStatus) {
        this.foodStatus = foodStatus;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

}
