package com.example.kitchen_assistant.models;

import android.text.format.DateUtils;
import android.util.Log;

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
import java.util.Locale;

@Parcel
public class Product {

    private static final String TAG = "product";
    private static final String DEFAULT_IMG = "https://cdn.dribbble.com/users/67525/screenshots/4517042/agarey_grocerydribbble.png";

    private static final String STATUS_BEST = "New & fresh";
    private static final String STATUS_GOOD = "Good to use";
    private static final String STATUS_SAFE = "Safe to use";
    private static final String STATUS_CLOSE = "Consume quickly!";
    private static final String STATUS_BAD = "Not safe to use";

    private static final String PRODUCT_INFO = "product";
    private static final String BARCODE = "code";
    private static final String NAME = "product_name";
    private static final String QUANTITY = "quantity";
    private static final String IMAGE_URL = "image_thumb_url";

    private String productCode;
    private String productName;
    private int originalQuantity;
    private int currentQuantity;
    private String quantityUnit;
    private int numProducts;
    private String imgUrl;
    private Date purchaseDate;
    private int duration;
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
            quantityUnit = null;
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

        Log.e(TAG, productCode);
        Log.e(TAG, productName);
        Log.e(TAG, "" + originalQuantity + " " + quantityUnit);
        Log.e(TAG, "" + currentQuantity + " " + quantityUnit);
        Log.e(TAG, "" + duration + " " + durationUnit);
        Log.e(TAG, String.valueOf(purchaseDate));
        Log.e(TAG, String.valueOf(expirationDate));
        Log.e(TAG, foodStatus);
        Log.e(TAG, imgUrl);
    }

    public void updateFoodStatus() {
        Date today = new Date();
        long difference = expirationDate.getTime() - today.getTime();
        long numDaysLeft = difference / (1000*60*60*24);
        long numDaysSafe = convertDurationToDays();
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

    private long convertDurationToDays() {
        long result = 0;
        switch (durationUnit) {
            case "year":
                Log.e(TAG, "YEAR");
                result = duration * 365;
            case "month":
                result = duration * 30;
            case "day":
                result = duration;
        }
        return result;
    }

    public void updateExpirationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(purchaseDate);
        switch (durationUnit) {
            case "year":
                Log.e(TAG, "YEAR");
                c.add(Calendar.YEAR, duration);
            case "month":
                c.add(Calendar.MONTH, duration);
            case "day":
                c.add(Calendar.DATE, duration);
        }
        expirationDate = c.getTime();
        return;
    }

    public void updateCurrentQuantity() {
        currentQuantity = numProducts * originalQuantity;
    }

    private String extractQuantityUnit(String quantityStr) {
        if (quantityStr.isEmpty()) {
            return null;
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

    public int getOriginalQuantity() {
        return originalQuantity;
    }

    public void setOriginalQuantity(int originalQuantity) {
        this.originalQuantity = originalQuantity;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public int getNumProducts() {
        return numProducts;
    }

    public void setNumProducts(int numProducts) {
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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
