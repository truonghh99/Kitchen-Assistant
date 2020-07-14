package com.example.kitchen_assistant.models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@ParseClassName("FoodItem")
public class FoodItem extends ParseObject implements Parcelable {

    private static final String KEY_NAME = "foodName";

    public String getName() {
        return getString(KEY_NAME);
    }
}
