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

    // Key for Parse
    private static final String KEY_NAME = "foodName";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_QUANTITY_UNIT = "quantityUnit";
    private static final String KEY_OWNER = "owner";

    // Local values
    private String name;
    private float quantity;
    private String quantityUnit;
    private ParseUser owner;

    public void fetchInfo() {
        name = getString(KEY_NAME);
        quantity = getNumber(KEY_QUANTITY).floatValue();
        quantityUnit = getString(KEY_QUANTITY_UNIT);
        owner = getParseUser(KEY_OWNER);
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

    public void increaseQuantity(Float currentQuantity, String quantityUnit) {
        float toIncrease = MetricConversionHelper.convertGeneral(currentQuantity, getQuantityUnit(), quantityUnit);
        setQuantity(getQuantity() + toIncrease);
    }
}
