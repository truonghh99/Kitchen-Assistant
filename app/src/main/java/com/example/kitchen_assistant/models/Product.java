package com.example.kitchen_assistant.models;

import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.kitchen_assistant.helpers.MetricConversionHelper;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@ParseClassName("Product")
public class Product extends ParseObject implements Parcelable {

    private static final String TAG = "product";
    private static final String DEFAULT_IMG = "https://cdn.dribbble.com/users/67525/screenshots/4517042/agarey_grocerydribbble.png";

    // Values of food status
    public static final String STATUS_BEST = "New & fresh";
    public static final String STATUS_SAFE = "Safe to use";
    public static final String STATUS_BAD = "Not safe to use";

    // Keyword for Open Food Facts json response
    private static final String PRODUCT_INFO = "product";
    private static final String BARCODE = "code";
    private static final String NAME = "product_name";
    private static final String QUANTITY = "quantity";
    private static final String IMAGE_URL = "image_thumb_url";

    // Keyword for Parse columns
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
    private static final String KEY_EXPIRATION_DATE = "expirationDate";
    private static final String KEY_FOOD_STATUS = "foodStatus";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_FOOD_TYPE = "foodType";

    public Product () {
    }

    // Extract product information from json returned by OpenFoodFacts
    public Product (JSONObject json) throws JSONException {
        Log.i(TAG, "Start extracting product information");
        JSONObject product = json.getJSONObject(PRODUCT_INFO);

        setProductCode(json.getString(BARCODE));
        setProductName( product.getString(NAME));
        try {
            String quantityStr = product.getString(QUANTITY);
            setOriginalQuantity(extractQuantityVal(quantityStr));
            setQuantityUnit(extractQuantityUnit(quantityStr));
        } catch (JSONException e) {
            setOriginalQuantity(0);
            setQuantityUnit("g");
        }
        try {
            setImgUrl(product.getString(IMAGE_URL));
        } catch (JSONException e) {
            setImgUrl(DEFAULT_IMG);
        }
        setPurchaseDate(new Date());
        setNumProducts(1);
        updateCurrentQuantity();
        setDuration(1);
        setDurationUnit("year");
        updateExpirationDate();
        updateFoodStatus();

        printOutValues();
    }

    public void printOutValues() {
        Log.i(TAG, "Product code: " + getProductCode());
        Log.i(TAG, "Product name: " + getProductName());
        Log.i(TAG, "Original quantity: " + getOriginalQuantity() + " " + getQuantityUnit());
        Log.i(TAG, "Number of items: " + getNumProducts());
        Log.i(TAG, "Current quantity: " + getCurrentQuantity() + " " + getQuantityUnit());
        Log.i(TAG, "Duration: " + getDuration() + " " + getDurationUnit());
        Log.i(TAG, "Purchase date: "  + String.valueOf(getPurchaseDate()));
        Log.i(TAG, "Expiration date: " + String.valueOf(getExpirationDate()));
        Log.i(TAG, "Food status: " + getFoodStatus());
        Log.i(TAG, "Image url: " + getImgUrl());
    }

    public void updateFoodStatus() {
        Date today = new Date();
        long difference = getExpirationDate().getTime() - today.getTime();
        long numDaysLeft = difference / (1000 * 60 * 60 * 24);
        long numDaysSafe = (long) MetricConversionHelper.convertTime(getDuration(), getDurationUnit(), "day");
        long ratio = numDaysLeft / numDaysSafe;
        if (ratio > 0.6) {
            setFoodStatus(STATUS_BEST);
            return;
        }
        if (ratio > 0.4) {
            setFoodStatus(STATUS_SAFE);
            return;
        }
        setFoodStatus(STATUS_BAD);
    }

    public void updateExpirationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(getPurchaseDate());
        switch (getDurationUnit()) {
            case "year":
                c.add(Calendar.YEAR, (int) getDuration());
            case "month":
                c.add(Calendar.MONTH, (int) getDuration());
            case "day":
                c.add(Calendar.DATE, (int) getDuration());
        }
        setExpirationDate(c.getTime());
        return;
    }

    public void updateCurrentQuantity() {
        setCurrentQuantity(getNumProducts() * getOriginalQuantity());
    }

    private String extractQuantityUnit(String quantityStr) {
        if (quantityStr.isEmpty()) {
            return "g";
        }
        int i = 0;
        while (quantityStr.charAt(i) <= '9' && quantityStr.charAt(i) >= '0') ++i;
        if (i < quantityStr.length() && quantityStr.charAt(i) == ' ') ++i;
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

    public String getProductCode() {
        return getString(KEY_CODE);
    }

    public void setProductCode(String productCode) {
        put(KEY_CODE, productCode);
    }

    public String getProductName() {
        return getString(KEY_NAME);
    }

    public void setProductName(String productName) {
        put(KEY_NAME, productName);
    }

    public float getOriginalQuantity() {
        return getNumber(KEY_ORIGINAL_QUANTITY).floatValue();
    }

    public void setOriginalQuantity(float originalQuantity) {
        put(KEY_ORIGINAL_QUANTITY, originalQuantity);
    }

    public float getCurrentQuantity() {
        return getNumber(KEY_CURRENT_QUANTITY).floatValue();
    }

    public void setCurrentQuantity(float currentQuantity) {
        put(KEY_CURRENT_QUANTITY, currentQuantity);
    }

    public String getQuantityUnit() {
        return getString(KEY_QUANTITY_UNIT);
    }

    public void setQuantityUnit(String quantityUnit) {
        put(KEY_QUANTITY_UNIT, quantityUnit);
    }

    public float getNumProducts() {
        return getNumber(KEY_NUM_PRODUCTS).floatValue();
    }

    public void setNumProducts(float numProducts) {
        put(KEY_NUM_PRODUCTS, numProducts);
    }

    public String getImgUrl() {
        return getString(KEY_IMG_URL);
    }

    public void setImgUrl(String imgUrl) {
        put(KEY_IMG_URL, imgUrl);
    }

    public Date getPurchaseDate() {
        return getDate(KEY_PURCHASE_DATE);
    }

    public void setPurchaseDate(Date purchaseDate) {
        put(KEY_PURCHASE_DATE, purchaseDate);
    }

    public float getDuration() {
        return getNumber(KEY_DURATION).floatValue();
    }

    public void setDuration(float duration) {
        put(KEY_DURATION, duration);
    }

    public String getDurationUnit() {
        return getString(KEY_DURATION_UNIT);
    }

    public void setDurationUnit(String durationUnit) {
       put(KEY_DURATION_UNIT, durationUnit);
    }

    public Date getExpirationDate() {
        return getDate(KEY_EXPIRATION_DATE);
    }

    public void setExpirationDate(Date expirationDate) {
        put(KEY_EXPIRATION_DATE, expirationDate);
    }

    public String getFoodStatus() {
        return getString(KEY_FOOD_STATUS);
    }

    public void setFoodStatus(String foodStatus) {
        put(KEY_FOOD_STATUS, foodStatus);
    }

    public ParseUser getOwner() {
        return getParseUser(KEY_OWNER);
    }

    public void setOwner(ParseUser owner) {
        put(KEY_OWNER, owner);
    }

    public FoodItem getFoodItem() {
        return (FoodItem) getParseObject(KEY_FOOD_TYPE);
    }

    public void setFoodItem(ParseObject foodItem) {
        put(KEY_FOOD_TYPE, foodItem);
    }

}
