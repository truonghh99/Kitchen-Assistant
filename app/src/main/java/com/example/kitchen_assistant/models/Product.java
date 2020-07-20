package com.example.kitchen_assistant.models;

import android.os.Parcelable;
import android.util.Log;

import com.example.kitchen_assistant.helpers.MetricConverter;
import com.example.kitchen_assistant.storage.CurrentFoodTypes;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    public static final String KEY_ID = "objectId";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_CODE = "productCode";
    public static final String KEY_NAME = "productName";
    public static final String KEY_ORIGINAL_QUANTITY = "originalQuantiy";
    public static final String KEY_CURRENT_QUANTITY = "currentQuantity";
    public static final String KEY_QUANTITY_UNIT = "quantityUnit";
    public static final String KEY_NUM_PRODUCTS = "numProducts";
    public static final String KEY_IMG_URL = "imageUrl";
    public static final String KEY_PURCHASE_DATE = "purchaseDate";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_DURATION_UNIT = "durationUnit";
    public static final String KEY_EXPIRATION_DATE = "expirationDate";
    public static final String KEY_FOOD_STATUS = "foodStatus";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_FOOD_TYPE = "foodType";

    // Local values
    private String productName;
    private String productCode;
    private float originalQuantity;
    private float currentQuantity;
    private String quantityUnit;
    private float numProducts;
    private String imageUrl;
    private Date purchaseDate;
    private float duration;
    private String durationUnit;
    private Date expirationDate;
    private String foodStatus;
    private ParseUser owner;
    private FoodItem foodItem;

    public Product () {
    }

    public void fetchInfo() {
        fetchInBackground();
        productName = getString(KEY_NAME);
        originalQuantity = getNumber(KEY_ORIGINAL_QUANTITY).floatValue();
        currentQuantity = getNumber(KEY_CURRENT_QUANTITY).floatValue();
        quantityUnit = getString(KEY_QUANTITY_UNIT);
        numProducts = getNumber(KEY_NUM_PRODUCTS).floatValue();
        imageUrl = getString(KEY_IMG_URL);
        purchaseDate = getDate(KEY_PURCHASE_DATE);
        duration = getNumber(KEY_DURATION).floatValue();
        durationUnit = getString(KEY_DURATION_UNIT);
        expirationDate = getDate(KEY_EXPIRATION_DATE);
        foodStatus = getString(KEY_FOOD_STATUS);
        owner = getParseUser(KEY_OWNER);
        foodItem = (FoodItem) getParseObject(KEY_FOOD_TYPE);
        productCode = getString(KEY_CODE);
    }

    public void saveInfo() {
        put(KEY_NAME, productName);
        Log.e(TAG, productCode);
        put(KEY_CODE, productCode);
        put(KEY_ORIGINAL_QUANTITY, originalQuantity);
        put(KEY_CURRENT_QUANTITY, currentQuantity);
        put(KEY_QUANTITY_UNIT, quantityUnit);
        put(KEY_NUM_PRODUCTS, numProducts);
        if (imageUrl != null) put(KEY_IMG_URL, imageUrl);
        put(KEY_PURCHASE_DATE, purchaseDate);
        put(KEY_DURATION, duration);
        put(KEY_DURATION_UNIT, durationUnit);
        put(KEY_EXPIRATION_DATE, expirationDate);
        put(KEY_FOOD_STATUS, foodStatus);
        put(KEY_FOOD_TYPE, foodItem);
        put(KEY_OWNER, ParseUser.getCurrentUser());
    }

    // Extract product information from json returned by OpenFoodFacts
    public Product (JSONObject json) throws JSONException {
        Log.i(TAG, "Start extracting product information");
        JSONObject product = json.getJSONObject(PRODUCT_INFO);

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
            setImageUrl(product.getString(IMAGE_URL));
        } catch (JSONException e) {
            setImageUrl(DEFAULT_IMG);
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
        Log.i(TAG, "Image url: " + getImageUrl());
    }

    public void updateFoodStatus() {
        Date today = new Date();
        long difference = getExpirationDate().getTime() - today.getTime();
        long numDaysLeft = difference / (1000 * 60 * 60 * 24);
        long numDaysSafe = (long) MetricConverter.convertTime(getDuration(), getDurationUnit(), "day");
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

    public void detachFoodItem() {
        Log.e(TAG, "Start detaching");
        FoodItem foodItem = getFoodItem();
        foodItem.increaseQuantity(getCurrentQuantity() * -1, getQuantityUnit());
        CurrentFoodTypes.saveFoodItemInBackGround(foodItem);
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public ParseUser getOwner() {
        return owner;
    }

    public void setOwnerId(ParseUser owner) {
        this.owner = owner;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(FoodItem foodType) {
        foodItem = foodType;
    }

    public String getFoodTypeString() {
        if (getFoodItem() != null) {
            return getFoodItem().getName();
        } else {
            return "undefined";
        }
    }

    public void subtractQuantity(float quantity, String quantityUnit) {
        float toSubtract = MetricConverter.convertGeneral(quantity, quantityUnit, getQuantityUnit());
        setCurrentQuantity(Math.max(0, getCurrentQuantity() - toSubtract));
        getFoodItem().subtractQuantity(toSubtract, getQuantityUnit());
    }
}
