package com.example.kitchen_assistant.models;

import android.text.format.DateUtils;
import android.util.Log;

import com.example.kitchen_assistant.helpers.MetricConversionHelper;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;

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

@Parcel
public class Product {

    private static final String TAG = "product";
    private static final String DEFAULT_IMG = "https://cdn.dribbble.com/users/67525/screenshots/4517042/agarey_grocerydribbble.png";

    public static final String STATUS_BEST = "New & fresh";
    public static final String STATUS_GOOD = "Good to use";
    public static final String STATUS_SAFE = "Safe to use";
    public static final String STATUS_CLOSE = "Consume quickly!";
    public static final String STATUS_BAD = "Not safe to use";

    private static final String PRODUCT_INFO = "product";
    private static final String BARCODE = "code";
    private static final String NAME = "product_name";
    private static final String QUANTITY = "quantity";
    private static final String IMAGE_URL = "image_thumb_url";

    private String productCode;
    private String productName;
    private float originalQuantity;
    private float currentQuantity;
    private String quantityUnit;
    private float numProducts;
    private String imgUrl;
    private Date purchaseDate;
    private float duration;
    private String durationUnit;
    private Date expirationDate;
    private String foodStatus;
    private User owner;
    private FoodItem foodItem;

    public Product () {
    }

    public Product (JSONObject json) throws JSONException, ParseException {
        Log.e(TAG, "START INIT");
        JSONObject product = json.getJSONObject(PRODUCT_INFO);

        productCode = json.getString(BARCODE);
        productName = product.getString(NAME);
        try {
            String quantityStr = product.getString(QUANTITY);
            originalQuantity = extractQuantityVal(quantityStr);
            quantityUnit = extractQuantityUnit(quantityStr);
        } catch (JSONException e) {
            originalQuantity = 0;
            quantityUnit = "g";
        }
        try {
            imgUrl = product.getString(IMAGE_URL);
        } catch (JSONException e) {
            imgUrl = DEFAULT_IMG;
        }
        purchaseDate = new Date();
        numProducts = 1;
        updateCurrentQuantity();
        duration = 1;
        durationUnit = "year";
        updateExpirationDate();
        updateFoodStatus();

        Log.i(TAG, "Product code: " + productCode);
        Log.i(TAG, "Product name: " + productName);
        Log.i(TAG, "Original quantity: " + originalQuantity + " " + quantityUnit);
        Log.i(TAG, "Current quantity: " + currentQuantity + " " + quantityUnit);
        Log.i(TAG, "Duration: " + duration + " " + durationUnit);
        Log.i(TAG, "Purchase date: "  + String.valueOf(purchaseDate));
        Log.i(TAG, "Expiration date: " + String.valueOf(expirationDate));
        Log.i(TAG, "Food status: " + foodStatus);
        Log.i(TAG, "Image url: " + imgUrl);
    }

    public void updateFoodStatus() {
        Date today = new Date();
        long difference = expirationDate.getTime() - today.getTime();
        long numDaysLeft = difference / (1000 * 60 * 60 * 24);
        long numDaysSafe = (long) MetricConversionHelper.convertTime(duration, durationUnit, "day");
        long ratio = numDaysLeft / numDaysSafe;
        if (ratio > 0.8) {
            foodStatus = STATUS_BEST;
            return;
        }
        if (ratio > 0.6) {
            foodStatus = STATUS_GOOD;
            return;
        }
        if (ratio > 0.4) {
            foodStatus = STATUS_SAFE;
            return;
        }
        if (ratio > 0.2) {
            foodStatus = STATUS_CLOSE;
            return;
        }
        foodStatus = STATUS_BAD;
    }

    public void updateExpirationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(purchaseDate);
        switch (durationUnit) {
            case "year":
                c.add(Calendar.YEAR, (int) duration);
            case "month":
                c.add(Calendar.MONTH, (int) duration);
            case "day":
                c.add(Calendar.DATE, (int) duration);
        }
        expirationDate = c.getTime();
        return;
    }

    public void updateCurrentQuantity() {
        currentQuantity = numProducts * originalQuantity;
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
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getOriginalQuantity() {
        return originalQuantity;
    }

    public void setOriginalQuantity(float originalQuantity) {
        this.originalQuantity = originalQuantity;
    }

    public float getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(float currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public float getNumProducts() {
        return numProducts;
    }

    public void setNumProducts(float numProducts) {
        this.numProducts = numProducts;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
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
