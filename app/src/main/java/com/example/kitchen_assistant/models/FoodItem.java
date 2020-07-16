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

    public String getName() throws ParseException {
        return fetchIfNeeded().getString(KEY_NAME);
    }
    public float getQuantity() {
        return getNumber(KEY_QUANTITY).floatValue();
    }
    public String getQuantityUnit() {
        return getString(KEY_QUANTITY_UNIT);
    }
    public ParseUser getOwner() {
        return getParseUser(KEY_OWNER);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }
    public void setQuantity(float quantity) {
        put(KEY_QUANTITY, quantity);
    }
    public void setQuantityUnit(String quantityUnit) {
        put(KEY_QUANTITY_UNIT, quantityUnit);
    }
    public void setOwner(ParseUser owner) {
        put(KEY_OWNER, owner);
    }

    public void increaseQuantity(Float currentQuantity, String quantityUnit) {
        float toIncrease = MetricConversionHelper.convertGeneral(currentQuantity, getQuantityUnit(), quantityUnit);
        setQuantity(getQuantity() + toIncrease);
    }
}
