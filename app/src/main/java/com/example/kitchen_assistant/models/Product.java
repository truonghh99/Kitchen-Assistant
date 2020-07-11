package com.example.kitchen_assistant.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Product {

    private static final String TAG = "product";

    private static final String PRODUCT_INFO = "product";
    private static final String BARCODE = "code";
    private static final String NAME = "product_name";
    private static final String QUANTITY = "quantity";
    private static final String IMAGE_URL = "image_thumb_url";
    private static final String EXPIRATION_DATE = "expiration_date";

    private String productId;
    private String productName;
    private String quantity;
    private String quantityUnit;
    private String imgUrl;
    private Date expirationDate;
    private Date purchaseDate;
    private String relativeDate;
    private User owner;
    private FoodItem foodItem;

    public Product () {
    }

    public Product (JSONObject json) throws JSONException, ParseException {
        JSONObject product = json.getJSONObject(PRODUCT_INFO);

        productId = json.getString(BARCODE);
        productName = product.getString(NAME);
        quantity = product.getString(QUANTITY);
        imgUrl = product.getString(IMAGE_URL);
        expirationDate = convertToDate(product.getString(EXPIRATION_DATE));
        purchaseDate = new Date();

        Log.e(TAG, productId);
        Log.e(TAG, product.toString());
        Log.e(TAG, productName);
        Log.e(TAG, quantity);
        Log.e(TAG, imgUrl);
        Log.e(TAG, String.valueOf(expirationDate));
        Log.e(TAG, String.valueOf(purchaseDate));
    }

    private Date convertToDate(String str) throws ParseException {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("DD/MM/YYYY", Locale.ENGLISH);
        if (str.isEmpty()) {
            return new Date(9000);
        }
        date = format.parse(str);
        return date;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
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
