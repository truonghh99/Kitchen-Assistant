package com.example.kitchen_assistant.models;

import android.os.Parcelable;

import com.example.kitchen_assistant.helpers.MetricConversionHelper;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("FoodItem")
public class FoodItem extends ParseObject implements Parcelable {

    private static final String KEY_NAME = "foodName";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_QUANTITY_UNIT = "quantityUnit";
    private static final String KEY_OWNER = "owner";

    private String name;
    private float quantity;
    private String quantityUnit;
    private String ownerId;

    public void fetchInfo() throws ParseException {
        name = fetchIfNeeded().getString(KEY_NAME);
        quantity = fetchIfNeeded().getNumber(KEY_QUANTITY).floatValue();
        quantityUnit = fetchIfNeeded().getString(KEY_QUANTITY_UNIT);
        ownerId = fetchIfNeeded().getParseUser(KEY_OWNER).getObjectId();
    }

    public void saveInfo() {
        put(KEY_NAME, name);
        put(KEY_QUANTITY, quantity);
        put(KEY_QUANTITY_UNIT, quantityUnit);
        saveInBackground();
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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

}
