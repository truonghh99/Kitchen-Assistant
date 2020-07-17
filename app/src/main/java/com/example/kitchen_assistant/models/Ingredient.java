package com.example.kitchen_assistant.models;

import android.os.Parcelable;
import android.util.Log;

import com.example.kitchen_assistant.helpers.MetricConversionHelper;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("Ingredient")
public class Ingredient extends ParseObject implements Parcelable {

    // Key for Parse
    public static final String KEY_NAME = "name";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_QUANTITY_UNIT = "quantityUnit";
    public static final String KEY_RECIPE = "recipe";
    public static final String TAG = "Ingredient";

    // Local values
    private String name;
    private float quantity;
    private String quantityUnit;
    private ParseObject recipe;

    public void fetchInfo() {
        name = getString(KEY_NAME);
        quantity = getNumber(KEY_QUANTITY).floatValue();
        quantityUnit = getString(KEY_QUANTITY_UNIT);
        recipe = getParseObject(KEY_RECIPE);
    }

    public void saveInfo() {
        put(KEY_NAME, name);
        put(KEY_QUANTITY, quantity);
        put(KEY_QUANTITY_UNIT, quantityUnit);
        put(KEY_RECIPE, ParseUser.getCurrentUser());
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

    public ParseObject getRecipe() {
        return recipe;
    }

    public void setRecipe (ParseObject object) {
        this.recipe = recipe;
    }

    public void increaseQuantity(Float currentQuantity, String quantityUnit) {
        Log.e(TAG, "Before adding: " + getQuantity());
        float toIncrease = MetricConversionHelper.convertGeneral(currentQuantity, getQuantityUnit(), quantityUnit);
        setQuantity(getQuantity() + toIncrease);
        Log.e(TAG, "After adding: " + getQuantity());
    }
}
