package com.example.kitchen_assistant.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.kitchen_assistant.helpers.MetricConverter;
import com.example.kitchen_assistant.helpers.Notification;
import com.example.kitchen_assistant.storage.CurrentFoodTypes;
import com.example.kitchen_assistant.storage.CurrentProducts;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@ParseClassName("Product")
public class Product extends ParseObject implements Parcelable {

    public static final String TAG = "product";
    private static final String DEFAULT_IMG = "default";
    // Values of food status
    public static final String STATUS_BEST = "good";
    public static final String STATUS_SAFE = "safe";
    public static final String STATUS_BAD = "bad";

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
    public static final String KEY_IMG = "image";
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
    private ParseFile parseFile;

    public Product () {
    }

    public void fetchInfo() {
        try {
            fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        productName = getString(KEY_NAME);
        originalQuantity = getNumber(KEY_ORIGINAL_QUANTITY).floatValue();
        currentQuantity = getNumber(KEY_CURRENT_QUANTITY).floatValue();
        quantityUnit = getString(KEY_QUANTITY_UNIT);
        numProducts = getNumber(KEY_NUM_PRODUCTS).floatValue();
        parseFile = getParseFile(KEY_IMG);
        if (parseFile != null) {
            imageUrl = parseFile.getUrl();
        }
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
        put(KEY_PURCHASE_DATE, purchaseDate);
        put(KEY_DURATION, duration);
        put(KEY_DURATION_UNIT, durationUnit);
        put(KEY_EXPIRATION_DATE, expirationDate);
        put(KEY_FOOD_STATUS, foodStatus);
        put(KEY_FOOD_TYPE, foodItem);
        put(KEY_OWNER, ParseUser.getCurrentUser());
        if (parseFile != null) put(KEY_IMG, parseFile);
    }

    public void saveImageFromUrl(String url) throws IOException {
        try {
            java.net.URL img_value = new java.net.URL(url);
            Bitmap mIcon = BitmapFactory
                    .decodeStream(img_value.openConnection()
                            .getInputStream());
            if (mIcon != null) {
                byte[] imgByteArray = encodeToByteArray(mIcon);
                this.parseFile = new ParseFile(imgByteArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] encodeToByteArray(Bitmap image) {
        Bitmap b = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imgByteArray = baos.toByteArray();

        return imgByteArray ;
    }

    public void saveImage(File file) {
        Log.e(TAG, file.toString());
        this.parseFile = new ParseFile(file);
    }

    // Extract product information from json returned by OpenFoodFacts
    public Product (JSONObject json) throws JSONException {
        Log.i(TAG, "Start extracting product information");
        JSONObject product;
        try {
            product = json.getJSONObject(PRODUCT_INFO);
        } catch (JSONException e) {
            return;
        }
        setProductName(product.getString(NAME));
        try {
            String quantityStr = product.getString(QUANTITY);
            setOriginalQuantity(MetricConverter.extractQuantityVal(quantityStr));
            setQuantityUnit(MetricConverter.extractQuantityUnit(quantityStr));
        } catch (JSONException e) {
            setOriginalQuantity(0);
            setQuantityUnit("unit");
        }
        try {
            setImageUrl(product.getString(IMAGE_URL));
        } catch (JSONException e) {
            setImageUrl(DEFAULT_IMG);
        }

        try {
            saveImageFromUrl(getImageUrl());
        } catch (IOException e) {
            e.printStackTrace();
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
        float difference = getExpirationDate().getTime() - today.getTime();
        float numDaysLeft = difference / (1000 * 60 * 60 * 24);
        float numDaysSafe = (long) MetricConverter.convertTime(getDuration(), getDurationUnit(), "day");
        float ratio = numDaysLeft / numDaysSafe;
        Log.e(TAG, "Ration: " + ratio);
        if (ratio >= 0.5) {
            setFoodStatus(STATUS_BEST);
        } else {
            if (ratio >= 0) {
                setFoodStatus(STATUS_SAFE);
            } else {
                setFoodStatus(STATUS_BAD);
            }
        }
    }

    public void updateExpirationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(getPurchaseDate());
        switch (getDurationUnit()) {
            case "year":
                c.add(Calendar.YEAR, (int) getDuration());
                break;
            case "years":
                c.add(Calendar.YEAR, (int) getDuration());
                break;
            case "month":
                c.add(Calendar.MONTH, (int) getDuration());
                break;
            case "months":
                c.add(Calendar.MONTH, (int) getDuration());
                break;
            case "day":
                c.add(Calendar.DATE, (int) getDuration());
                break;
            case "days":
                c.add(Calendar.DATE, (int) getDuration());
                break;
        }
        setExpirationDate(c.getTime());
        return;
    }

    public void updateCurrentQuantity() {
        setCurrentQuantity(getNumProducts() * getOriginalQuantity());
    }

    public void detachFoodItem() {
        Log.e(TAG, "Start detaching");
        FoodItem foodItem = getFoodItem();
        foodItem.addQuantity(getCurrentQuantity() * -1, getQuantityUnit());
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
        this.foodStatus = foodStatus.toLowerCase();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void subtractQuantity(float quantity, String quantityUnit, Context context) {
        float toSubtract = MetricConverter.convertGeneral(quantity, quantityUnit, getQuantityUnit());
        setCurrentQuantity(Math.max(0, getCurrentQuantity() - toSubtract));
        getFoodItem().subtractQuantity(toSubtract, getQuantityUnit());

        if (currentQuantity <= originalQuantity * 0.01) {
            Notification.createShoppingNotification(this, context);
        }
    }

    public void setParseFile(ParseFile parseFile) {
        this.parseFile = parseFile;
        put(KEY_IMG, parseFile);
        try {
            save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setImageUrl(parseFile.getUrl());
    }

    public ParseFile getParseFile() {
        return parseFile;
    }

    public void addQuantity(float quantity, String quantityUnit) {
        float toAdd = MetricConverter.convertGeneral(quantity, quantityUnit, getQuantityUnit());
        setCurrentQuantity(Math.max(0, getCurrentQuantity() + toAdd));
        getFoodItem().addQuantity(toAdd, getQuantityUnit());
    }
}