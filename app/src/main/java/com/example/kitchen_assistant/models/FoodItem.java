package com.example.kitchen_assistant.models;

import android.os.Parcelable;
import android.util.Log;

import com.example.kitchen_assistant.helpers.MetricConverter;
import com.example.kitchen_assistant.storage.CurrentFoodTypes;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ParseClassName("FoodItem")
public class FoodItem extends ParseObject implements Parcelable {

    // Key for Parse
    private static final String KEY_NAME = "foodName";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_QUANTITY_UNIT = "quantityUnit";
    private static final String KEY_OWNER = "owner";
    private static final String TAG = "FoodItem";

    // Local values
    private String name;
    private float quantity;
    private String quantityUnit;
    private ParseUser owner;
    private List<Product> products = new ArrayList<>();

    public void fetchInfo() throws ParseException {
        name = fetchIfNeeded().getString(KEY_NAME);
        quantity = fetchIfNeeded().getNumber(KEY_QUANTITY).floatValue();
        quantityUnit = fetchIfNeeded().getString(KEY_QUANTITY_UNIT);
        owner = fetchIfNeeded().getParseUser(KEY_OWNER);
    }

    public void saveInfo() {
        put(KEY_NAME, name);
        put(KEY_QUANTITY, quantity);
        put(KEY_QUANTITY_UNIT, quantityUnit);
        put(KEY_OWNER, ParseUser.getCurrentUser());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public ParseUser getOwner() {
        return owner;
    }

    public void setOwner(ParseUser owner) {
        this.owner = owner;
    }

    public void addQuantity(Float currentQuantity, String quantityUnit) {
        Log.e(TAG, "Before adding: " + getQuantity());
        float toIncrease = MetricConverter.convertGeneral(currentQuantity, getQuantityUnit(), quantityUnit);
        setQuantity(getQuantity() + toIncrease);
        Log.e(TAG, "After adding: " + getQuantity());
    }

    public void addProductToType(Product product) {
        products.add(product);
    }

    public List<Product> getProductList() {
        return products;
    }

    public Product getFirstProduct() {
        return products.get(0);
    }

    public void subtractQuantity(float quantity, String quantityUnit) {
        float toSubtract = MetricConverter.convertGeneral(quantity, quantityUnit, getQuantityUnit());
        setQuantity(Math.max(0, getQuantity() - toSubtract));
    }
}
